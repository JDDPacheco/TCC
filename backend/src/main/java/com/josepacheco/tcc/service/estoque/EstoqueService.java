package com.josepacheco.tcc.service.estoque;

import com.josepacheco.tcc.dto.estoque.AjusteEstoqueInputDTO;
import com.josepacheco.tcc.dto.estoque.EntradaEstoqueInputDTO;
import com.josepacheco.tcc.dto.estoque.LoteOutputDTO;
import com.josepacheco.tcc.dto.estoque.MovimentacaoEstoqueInputDTO;
import com.josepacheco.tcc.dto.estoque.MovimentacaoEstoqueOutputDTO;
import com.josepacheco.tcc.dto.estoque.SaldoProdutoOutputDTO;
import com.josepacheco.tcc.model.estoque.Lote;
import com.josepacheco.tcc.model.estoque.MovimentacaoEstoque;
import com.josepacheco.tcc.model.estoque.TipoMovimentacao;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.repository.estoque.LoteRepository;
import com.josepacheco.tcc.repository.estoque.MovimentacaoEstoqueRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    /** Código do lote criado automaticamente para produtos gerais (que não têm lote nem validade). */
    public static final String CODIGO_LOTE_UNICO = "UNICO";

    /** Prazo, em dias, a partir do qual um lote é considerado "a vencer". */
    public static final int DIAS_ALERTA_VENCIMENTO = 30;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private LoteRepository loteRepository;
    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    // ==========================================================
    // ENTRADA DE MERCADORIA
    // ==========================================================

    @Transactional
    public MovimentacaoEstoqueOutputDTO registrarEntrada(EntradaEstoqueInputDTO entrada) {
        Produto produto = buscarProduto(entrada.getEan());
        Lote lote = ehRemedio(produto) ? obterLoteDeRemedio(produto, entrada) : obterLoteUnico(produto);

        MovimentacaoEstoque movimentacao = movimentar(lote, TipoMovimentacao.ENTRADA, entrada.getQuantidade(),
                textoOuNulo(entrada.getObservacao()), entrada.getCustoUnitario(), entrada.getFornecedor().trim());
        return new MovimentacaoEstoqueOutputDTO(movimentacao);
    }

    // Remédios exigem código de lote e validade; entradas repetidas no mesmo lote somam ao saldo
    private Lote obterLoteDeRemedio(Produto produto, EntradaEstoqueInputDTO entrada) {
        String codigoLote = textoOuNulo(entrada.getCodigoLote());
        LocalDate validade = entrada.getDataValidade();
        if (codigoLote == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O código do lote é obrigatório para remédios.");
        if (validade == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de validade é obrigatória para remédios.");
        if (validade.isBefore(hoje()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível dar entrada em lote vencido (validade " + validade.format(FORMATO_DATA) + ").");

        Lote lote = loteRepository.findByProdutoAndCodigoLote(produto, codigoLote).orElse(null);
        if (lote == null)
            return loteRepository.save(new Lote(produto, codigoLote, validade));
        if (!validade.equals(lote.getDataValidade()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O lote " + codigoLote + " deste produto já está cadastrado com validade "
                    + lote.getDataValidade().format(FORMATO_DATA) + ". Confira a data informada.");
        return lote;
    }

    // Produtos gerais (sem lote/validade na embalagem) usam um único lote por produto
    private Lote obterLoteUnico(Produto produto) {
        return loteRepository.findByProdutoAndCodigoLote(produto, CODIGO_LOTE_UNICO)
                .orElseGet(() -> loteRepository.save(new Lote(produto, CODIGO_LOTE_UNICO, null)));
    }

    // ==========================================================
    // AJUSTES, PERDAS E DEVOLUÇÕES
    // ==========================================================

    @Transactional
    public MovimentacaoEstoqueOutputDTO registrarAjuste(AjusteEstoqueInputDTO ajuste) {
        Lote lote = buscarLote(ajuste.getIdLote());
        int diferenca = ajuste.getQuantidadeContada() - lote.getQuantidadeAtual();
        if (diferenca == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A quantidade contada é igual ao saldo do lote (" + lote.getQuantidadeAtual() + "); não há o que ajustar.");
        return new MovimentacaoEstoqueOutputDTO(
                movimentar(lote, TipoMovimentacao.AJUSTE_INVENTARIO, diferenca, ajuste.getObservacao().trim(), null, null));
    }

    @Transactional
    public MovimentacaoEstoqueOutputDTO registrarPerda(MovimentacaoEstoqueInputDTO perda) {
        Lote lote = buscarLote(perda.getIdLote());
        if (perda.getQuantidade() > lote.getQuantidadeAtual())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A perda informada (" + perda.getQuantidade()
                    + ") é maior que o saldo do lote (" + lote.getQuantidadeAtual() + ").");
        return new MovimentacaoEstoqueOutputDTO(
                movimentar(lote, TipoMovimentacao.PERDA, -perda.getQuantidade(), perda.getObservacao().trim(), null, null));
    }

    @Transactional
    public MovimentacaoEstoqueOutputDTO registrarDevolucao(MovimentacaoEstoqueInputDTO devolucao) {
        Lote lote = buscarLote(devolucao.getIdLote());
        return new MovimentacaoEstoqueOutputDTO(
                movimentar(lote, TipoMovimentacao.DEVOLUCAO, devolucao.getQuantidade(), devolucao.getObservacao().trim(), null, null));
    }

    // ==========================================================
    // BAIXA POR VENDA (FEFO) — chamada pelo módulo de vendas
    // ==========================================================

    /**
     * Retira a quantidade do estoque do produto consumindo primeiro os lotes que vencem antes (FEFO),
     * ignorando lotes vencidos. Pode dividir a baixa entre vários lotes.
     * Deve ser chamada dentro da transação da venda: se a venda falhar, a baixa é desfeita junto.
     *
     * @return os lotes de onde a mercadoria saiu, com a quantidade retirada de cada um
     */
    @Transactional
    public List<LoteBaixado> baixarEstoque(String ean, int quantidade, String observacao) {
        if (quantidade <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quantidade da baixa deve ser maior que zero.");
        Produto produto = buscarProduto(ean);

        List<Lote> lotesDisponiveis = loteRepository.findDisponiveisParaBaixa(produto, hoje());
        int disponivel = lotesDisponiveis.stream().mapToInt(Lote::getQuantidadeAtual).sum();
        if (disponivel < quantidade)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente para " + produto.getNomeComercial()
                    + ": solicitado " + quantidade + ", disponível " + disponivel + ".");

        List<LoteBaixado> baixas = new ArrayList<>();
        int restante = quantidade;
        for (Lote lote : lotesDisponiveis) {
            if (restante == 0) break;
            int retirada = Math.min(restante, lote.getQuantidadeAtual());
            movimentar(lote, TipoMovimentacao.SAIDA_VENDA, -retirada, observacao, null, null);
            baixas.add(new LoteBaixado(lote, retirada));
            restante -= retirada;
        }
        return baixas;
    }

    // ==========================================================
    // CONSULTAS E ALERTAS
    // ==========================================================

    @Transactional(readOnly = true)
    public SaldoProdutoOutputDTO consultarSaldo(String ean) {
        Produto produto = buscarProduto(ean);
        return new SaldoProdutoOutputDTO(produto, loteRepository.findByProdutoOrdenadosPorValidade(produto), hoje(), DIAS_ALERTA_VENCIMENTO);
    }

    @Transactional(readOnly = true)
    public List<SaldoProdutoOutputDTO> listarSaldos() {
        LocalDate hoje = hoje();
        // Carrega todos os lotes de uma vez e agrupa por produto (evita uma consulta por produto)
        Map<Long, List<Lote>> lotesPorProduto = loteRepository.findAll().stream()
                .sorted(Comparator.comparing(Lote::getDataValidade, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.groupingBy(l -> l.getProduto().getId()));
        return produtoRepository.findAll().stream()
                .sorted(Comparator.comparing(Produto::getNomeComercial, String.CASE_INSENSITIVE_ORDER))
                .map(p -> new SaldoProdutoOutputDTO(p, lotesPorProduto.getOrDefault(p.getId(), List.of()), hoje, DIAS_ALERTA_VENCIMENTO))
                .toList();
    }

    @Transactional
    public SaldoProdutoOutputDTO definirEstoqueMinimo(String ean, Integer estoqueMinimo) {
        Produto produto = buscarProduto(ean);
        produto.setEstoqueMinimo(estoqueMinimo);
        produtoRepository.save(produto);
        return consultarSaldo(ean);
    }

    @Transactional(readOnly = true)
    public List<LoteOutputDTO> listarLotesAVencer(int dias) {
        if (dias < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O número de dias não pode ser negativo.");
        LocalDate hoje = hoje();
        return loteRepository.findComSaldoVencendoEntre(hoje, hoje.plusDays(dias)).stream()
                .map(l -> new LoteOutputDTO(l, hoje, DIAS_ALERTA_VENCIMENTO))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoteOutputDTO> listarLotesVencidos() {
        LocalDate hoje = hoje();
        return loteRepository.findComSaldoVencidos(hoje).stream()
                .map(l -> new LoteOutputDTO(l, hoje, DIAS_ALERTA_VENCIMENTO))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaldoProdutoOutputDTO> listarEstoqueBaixo() {
        return listarSaldos().stream().filter(SaldoProdutoOutputDTO::isAbaixoDoMinimo).toList();
    }

    /** Histórico de movimentações, do mais recente para o mais antigo. Filtra por lote, por produto ou traz tudo. */
    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueOutputDTO> listarMovimentacoes(String ean, Long idLote) {
        List<MovimentacaoEstoque> movimentacoes;
        if (idLote != null)
            movimentacoes = movimentacaoRepository.findByLoteOrderByDataHoraDescIdDesc(buscarLote(idLote));
        else if (ean != null && !ean.isBlank())
            movimentacoes = movimentacaoRepository.findByLoteProdutoOrderByDataHoraDescIdDesc(buscarProduto(ean));
        else
            movimentacoes = movimentacaoRepository.findAllByOrderByDataHoraDescIdDesc();
        return movimentacoes.stream().map(MovimentacaoEstoqueOutputDTO::new).toList();
    }

    // ==========================================================
    // MÉTODOS AUXILIARES
    // ==========================================================

    // Único ponto que altera saldo: atualiza o lote e registra a movimentação na mesma transação
    private MovimentacaoEstoque movimentar(Lote lote, TipoMovimentacao tipo, int quantidade, String observacao,
                                           BigDecimal custoUnitario, String fornecedor) {
        int novoSaldo = lote.getQuantidadeAtual() + quantidade;
        if (novoSaldo < 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Saldo insuficiente no lote " + lote.getCodigoLote() + ".");
        lote.setQuantidadeAtual(novoSaldo);
        loteRepository.save(lote);
        return movimentacaoRepository.save(new MovimentacaoEstoque(lote, tipo, quantidade, novoSaldo, LocalDateTime.now(),
                observacao, custoUnitario, fornecedor));
    }

    private Produto buscarProduto(String ean) {
        return produtoRepository.findByEan(ean.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado com EAN: " + ean));
    }

    private Lote buscarLote(Long idLote) {
        return loteRepository.findById(idLote)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado com id: " + idLote));
    }

    // O produto pode ser um proxy do Hibernate; o tipo real só é visível depois de "desembrulhá-lo"
    private boolean ehRemedio(Produto produto) {
        return Hibernate.unproxy(produto) instanceof Remedio;
    }

    private String textoOuNulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    private LocalDate hoje() {
        return LocalDate.now();
    }
}
