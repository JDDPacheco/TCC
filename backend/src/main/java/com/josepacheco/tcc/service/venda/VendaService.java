package com.josepacheco.tcc.service.venda;

import com.josepacheco.tcc.dto.venda.ItemVendaInputDTO;
import com.josepacheco.tcc.dto.venda.ProdutoPdvOutputDTO;
import com.josepacheco.tcc.dto.venda.ResumoVendasOutputDTO;
import com.josepacheco.tcc.dto.venda.ResumoVendasOutputDTO.TotalPorFormaPagamento;
import com.josepacheco.tcc.dto.venda.VendaInputDTO;
import com.josepacheco.tcc.dto.venda.VendaOutputDTO;
import com.josepacheco.tcc.model.preco.Preco;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.remedio.Generico;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.model.venda.DadosReceita;
import com.josepacheco.tcc.model.venda.FormaPagamento;
import com.josepacheco.tcc.model.venda.ItemVenda;
import com.josepacheco.tcc.model.venda.StatusVenda;
import com.josepacheco.tcc.model.venda.Venda;
import com.josepacheco.tcc.repository.preco.PrecoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import com.josepacheco.tcc.repository.venda.VendaRepository;
import com.josepacheco.tcc.service.estoque.EstoqueService;
import com.josepacheco.tcc.service.estoque.LoteBaixado;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VendaService {

    /** Tipos de controle (ControleReceita.tipoControle) que exigem receita na venda. */
    public static final String CONTROLE_SOB_PRESCRICAO = "sob_prescricao";
    public static final String CONTROLE_ESPECIAL = "especial";

    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private EstoqueService estoqueService;

    // Item já validado, aguardando a gravação da venda
    private record ItemValidado(Produto produto, int quantidade, BigDecimal precoUnitario, String tipoControle, DadosReceita receita) {
        BigDecimal subtotal() {
            return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        }
    }

    // ==========================================================
    // PDV: CONSULTA DE PRODUTO
    // ==========================================================

    /** Tudo o que o caixa precisa para colocar o produto no carrinho: preço atual, saldo e exigência de receita. */
    @Transactional(readOnly = true)
    public ProdutoPdvOutputDTO consultarProdutoParaVenda(String ean) {
        Produto produto = buscarProduto(ean);
        BigDecimal precoAtual = precoAtual(produto.getEan());
        int saldoDisponivel = estoqueService.consultarSaldo(produto.getEan()).getSaldoDisponivel();
        String controle = tipoControle(produto);
        return new ProdutoPdvOutputDTO(produto.getEan(), produto.getNomeComercial(), tipoProduto(produto),
                produto.getUnidadeDeMedida().getSigla(), precoAtual, saldoDisponivel, controle,
                exigeReceita(controle), CONTROLE_ESPECIAL.equals(controle));
    }

    // ==========================================================
    // REGISTRO DA VENDA
    // ==========================================================

    /**
     * Registra a venda em uma única transação: valida todos os itens (preço, estoque e receita), o desconto
     * e o pagamento antes de gravar qualquer coisa; depois grava a venda e dá baixa no estoque por FEFO,
     * criando um ItemVenda para cada lote usado. Qualquer falha desfaz tudo.
     */
    @Transactional
    public VendaOutputDTO registrarVenda(VendaInputDTO entrada) {
        // 1) Validação completa, sem gravar nada
        List<ItemValidado> itens = new ArrayList<>();
        Set<String> eansDaVenda = new HashSet<>();
        for (ItemVendaInputDTO item : entrada.getItens()) {
            String ean = item.getEan().trim();
            if (!eansDaVenda.add(ean))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto com EAN " + ean
                        + " aparece mais de uma vez na venda; informe a quantidade total em um único item.");
            itens.add(validarItem(ean, item));
        }

        BigDecimal valorBruto = itens.stream().map(ItemValidado::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal desconto = emReais(entrada.getDesconto() == null ? BigDecimal.ZERO : entrada.getDesconto());
        if (desconto.compareTo(valorBruto) > 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O desconto (R$ " + desconto
                    + ") não pode ser maior que o valor da venda (R$ " + valorBruto + ").");
        BigDecimal valorTotal = valorBruto.subtract(desconto);

        BigDecimal valorRecebido = null;
        BigDecimal troco = null;
        if (entrada.getFormaPagamento() == FormaPagamento.DINHEIRO) {
            if (entrada.getValorRecebido() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o valor recebido para pagamento em dinheiro.");
            valorRecebido = emReais(entrada.getValorRecebido());
            if (valorRecebido.compareTo(valorTotal) < 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor recebido (R$ " + valorRecebido
                        + ") é menor que o total da venda (R$ " + valorTotal + ").");
            troco = valorRecebido.subtract(valorTotal);
        }

        // 2) Gravação: a venda é salva primeiro para gerar o número usado nas movimentações de estoque
        Venda venda = new Venda();
        venda.setFormaPagamento(entrada.getFormaPagamento());
        venda.setValorBruto(valorBruto);
        venda.setDesconto(desconto);
        venda.setValorTotal(valorTotal);
        venda.setValorRecebido(valorRecebido);
        venda.setTroco(troco);
        venda = vendaRepository.save(venda);

        String observacao = "Venda nº " + venda.getId();
        for (ItemValidado item : itens) {
            // A baixa revalida o saldo com os lotes travados (protege contra vendas simultâneas)
            for (LoteBaixado baixa : estoqueService.baixarEstoque(item.produto().getEan(), item.quantidade(), observacao)) {
                ItemVenda itemVenda = new ItemVenda();
                itemVenda.setProduto(item.produto());
                itemVenda.setLote(baixa.lote());
                itemVenda.setQuantidade(baixa.quantidade());
                itemVenda.setPrecoUnitarioCobrado(item.precoUnitario());
                itemVenda.setTipoControle(item.tipoControle());
                itemVenda.setReceitaApresentada(item.tipoControle() != null ? Boolean.TRUE : null);
                itemVenda.setReceita(item.receita());
                venda.adicionarItem(itemVenda);
            }
        }
        return new VendaOutputDTO(vendaRepository.saveAndFlush(venda));
    }

    private ItemValidado validarItem(String ean, ItemVendaInputDTO item) {
        Produto produto = buscarProduto(ean);

        BigDecimal preco = precoAtual(ean);
        if (preco == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O produto " + produto.getNomeComercial()
                    + " não tem preço cadastrado. Cadastre o preço antes de vendê-lo.");

        int disponivel = estoqueService.consultarSaldo(ean).getSaldoDisponivel();
        if (disponivel < item.getQuantidade())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente para " + produto.getNomeComercial()
                    + ": solicitado " + item.getQuantidade() + ", disponível " + disponivel + ".");

        String controle = tipoControle(produto);
        if (!exigeReceita(controle))
            return new ItemValidado(produto, item.getQuantidade(), preco, null, null); // dados de receita enviados são ignorados

        if (!Boolean.TRUE.equals(item.getReceitaApresentada()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto " + produto.getNomeComercial()
                    + " exige receita: confirme que ela foi apresentada.");
        if (CONTROLE_ESPECIAL.equals(controle)) {
            if (item.getReceita() == null || !item.getReceita().isCompleta())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto " + produto.getNomeComercial()
                        + " é de controle especial: informe o nome do paciente, o nome e o registro do prescritor e o número da receita.");
            return new ItemValidado(produto, item.getQuantidade(), preco, controle, item.getReceita().build());
        }
        return new ItemValidado(produto, item.getQuantidade(), preco, controle, null);
    }

    // ==========================================================
    // CANCELAMENTO
    // ==========================================================

    /** Cancela a venda inteira: marca como cancelada (não apaga) e devolve cada item ao lote de onde saiu. */
    @Transactional
    public VendaOutputDTO cancelarVenda(Long idVenda, String motivo) {
        Venda venda = buscarVenda(idVenda);
        if (venda.getStatus() == StatusVenda.CANCELADA)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A venda nº " + idVenda + " já foi cancelada.");

        String observacao = "Cancelamento da venda nº " + idVenda + ": " + motivo.trim();
        for (ItemVenda item : venda.getItens())
            estoqueService.estornarBaixa(item.getLote(), item.getQuantidade(), observacao);

        venda.setStatus(StatusVenda.CANCELADA);
        venda.setDataCancelamento(LocalDateTime.now());
        venda.setMotivoCancelamento(motivo.trim());
        return new VendaOutputDTO(vendaRepository.save(venda));
    }

    // ==========================================================
    // CONSULTAS
    // ==========================================================

    @Transactional(readOnly = true)
    public VendaOutputDTO consultarVenda(Long idVenda) {
        return new VendaOutputDTO(buscarVenda(idVenda));
    }

    /** Vendas (concluídas e canceladas) entre as datas informadas, inclusive; sem datas, as de hoje. */
    @Transactional(readOnly = true)
    public List<VendaOutputDTO> listarVendas(LocalDate inicio, LocalDate fim) {
        LocalDate dataInicial = inicio != null ? inicio : LocalDate.now();
        LocalDate dataFinal = fim != null ? fim : dataInicial;
        if (dataFinal.isBefore(dataInicial))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data final não pode ser anterior à data inicial.");
        return vendasEntre(dataInicial, dataFinal).stream().map(VendaOutputDTO::new).toList();
    }

    /** Fechamento de caixa de um dia (padrão: hoje). Valores consideram apenas vendas concluídas. */
    @Transactional(readOnly = true)
    public ResumoVendasOutputDTO resumoDoDia(LocalDate data) {
        LocalDate dia = data != null ? data : LocalDate.now();
        List<Venda> vendas = vendasEntre(dia, dia);
        List<Venda> concluidas = vendas.stream().filter(v -> v.getStatus() == StatusVenda.CONCLUIDA).toList();

        BigDecimal valorBruto = somar(concluidas.stream().map(Venda::getValorBruto).toList());
        BigDecimal descontos = somar(concluidas.stream().map(Venda::getDesconto).toList());
        BigDecimal valorTotal = somar(concluidas.stream().map(Venda::getValorTotal).toList());
        BigDecimal ticketMedio = concluidas.isEmpty() ? BigDecimal.ZERO.setScale(2)
                : valorTotal.divide(BigDecimal.valueOf(concluidas.size()), 2, RoundingMode.HALF_UP);

        List<TotalPorFormaPagamento> porForma = Arrays.stream(FormaPagamento.values())
                .map(forma -> {
                    List<Venda> daForma = concluidas.stream().filter(v -> v.getFormaPagamento() == forma).toList();
                    return new TotalPorFormaPagamento(forma.name(), daForma.size(), somar(daForma.stream().map(Venda::getValorTotal).toList()));
                })
                .toList();

        return new ResumoVendasOutputDTO(dia, concluidas.size(), vendas.size() - concluidas.size(),
                valorBruto, descontos, valorTotal, ticketMedio, porForma);
    }

    // ==========================================================
    // MÉTODOS AUXILIARES
    // ==========================================================

    private List<Venda> vendasEntre(LocalDate inicio, LocalDate fim) {
        return vendaRepository.findByDataVendaGreaterThanEqualAndDataVendaLessThanOrderByDataVendaDescIdDesc(
                inicio.atStartOfDay(), fim.plusDays(1).atStartOfDay());
    }

    private BigDecimal precoAtual(String ean) {
        return precoRepository.findTopByProdutoEanOrderByDataInicioVigenciaDesc(ean).map(Preco::getValorVenda).orElse(null);
    }

    // O produto pode ser um proxy do Hibernate; o tipo real só é visível depois de "desembrulhá-lo"
    private String tipoControle(Produto produto) {
        if (Hibernate.unproxy(produto) instanceof Remedio remedio && remedio.getControle() != null)
            return remedio.getControle().getTipoControle();
        return null;
    }

    private String tipoProduto(Produto produto) {
        Object real = Hibernate.unproxy(produto);
        if (real instanceof Generico) return "generico";
        if (real instanceof Remedio) return "remedio";
        return "geral";
    }

    private boolean exigeReceita(String tipoControle) {
        return CONTROLE_SOB_PRESCRICAO.equals(tipoControle) || CONTROLE_ESPECIAL.equals(tipoControle);
    }

    private BigDecimal emReais(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal somar(List<BigDecimal> valores) {
        return valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    private Produto buscarProduto(String ean) {
        return produtoRepository.findByEan(ean.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado com EAN: " + ean));
    }

    private Venda buscarVenda(Long idVenda) {
        return vendaRepository.findById(idVenda)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada com número: " + idVenda));
    }
}
