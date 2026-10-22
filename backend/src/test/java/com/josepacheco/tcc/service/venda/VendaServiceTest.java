package com.josepacheco.tcc.service.venda;

import com.josepacheco.tcc.dto.estoque.EntradaEstoqueInputDTO;
import com.josepacheco.tcc.dto.preco.PrecoInputDTO;
import com.josepacheco.tcc.dto.venda.DadosReceitaDTO;
import com.josepacheco.tcc.dto.venda.ItemVendaInputDTO;
import com.josepacheco.tcc.dto.venda.ItemVendaOutputDTO;
import com.josepacheco.tcc.dto.venda.ProdutoPdvOutputDTO;
import com.josepacheco.tcc.dto.venda.ResumoVendasOutputDTO;
import com.josepacheco.tcc.dto.venda.VendaInputDTO;
import com.josepacheco.tcc.dto.venda.VendaOutputDTO;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.model.venda.FormaPagamento;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ControleReceitaRepository;
import com.josepacheco.tcc.repository.venda.VendaRepository;
import com.josepacheco.tcc.service.estoque.EstoqueService;
import com.josepacheco.tcc.service.preco.PrecoService;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Regras de venda sobre H2 em memória, integradas aos módulos de preço e estoque.
 * Cada teste roda em uma transação desfeita ao final.
 */
@SpringBootTest
@Transactional
class VendaServiceTest {

    private static final String EAN_GERAL = "7890000000501";      // R$ 5,00 · 20 UN
    private static final String EAN_ISENTO = "7890000000502";     // R$ 12,90 · lotes CEDO (3) e TARDE (10)
    private static final String EAN_PRESCRICAO = "7890000000503"; // R$ 30,00 · 5 CX · sob prescrição
    private static final String EAN_ESPECIAL = "7890000000504";   // R$ 45,00 · 5 CX · controle especial
    private static final String EAN_SEM_PRECO = "7890000000505";  // sem preço · 5 CX

    @Autowired
    private VendaService vendaService;
    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private EstoqueService estoqueService;
    @Autowired
    private PrecoService precoService;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MedidaPadraoRepository medidaPadraoRepository;
    @Autowired
    private ControleReceitaRepository controleReceitaRepository;

    private final LocalDate hoje = LocalDate.now();

    @BeforeEach
    void prepararProdutosPrecosEEstoque() {
        produtoRepository.save(new Produto("Escova Dental", EAN_GERAL, medidaPadraoRepository.findBySigla("UN")));
        criarRemedio(EAN_ISENTO, "Dipirona 1g", "isento");
        criarRemedio(EAN_PRESCRICAO, "Amoxicilina 500mg", "sob_prescricao");
        criarRemedio(EAN_ESPECIAL, "Clonazepam 2mg", "especial");
        criarRemedio(EAN_SEM_PRECO, "Remédio Sem Preço", "isento");

        cadastrarPreco(EAN_GERAL, "5.00");
        cadastrarPreco(EAN_ISENTO, "12.90");
        cadastrarPreco(EAN_PRESCRICAO, "30.00");
        cadastrarPreco(EAN_ESPECIAL, "45.00");

        darEntrada(EAN_GERAL, null, null, 20);
        darEntrada(EAN_ISENTO, "TARDE", hoje.plusMonths(10), 10);
        darEntrada(EAN_ISENTO, "CEDO", hoje.plusMonths(2), 3);
        darEntrada(EAN_PRESCRICAO, "P1", hoje.plusMonths(6), 5);
        darEntrada(EAN_ESPECIAL, "E1", hoje.plusMonths(6), 5);
        darEntrada(EAN_SEM_PRECO, "S1", hoje.plusMonths(6), 5);
    }

    // ---------------------------------------------------------- registro da venda

    @Test
    void vendaUsaOPrecoAtualEDaBaixaPorFefoComUmItemPorLote() {
        VendaOutputDTO venda = vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_ISENTO, 5)));

        assertThat(venda.getId()).isNotNull();
        assertThat(venda.getStatus()).isEqualTo("CONCLUIDA");
        assertThat(venda.getItens()).extracting(ItemVendaOutputDTO::getCodigoLote, ItemVendaOutputDTO::getQuantidade)
                .containsExactly(Tuple.tuple("CEDO", 3), Tuple.tuple("TARDE", 2));
        assertThat(venda.getItens()).allSatisfy(i -> assertThat(i.getPrecoUnitario()).isEqualByComparingTo("12.90"));
        assertThat(venda.getValorBruto()).isEqualByComparingTo("64.50");
        assertThat(venda.getDesconto()).isEqualByComparingTo("0");
        assertThat(venda.getValorTotal()).isEqualByComparingTo("64.50");
        assertThat(venda.getValorRecebido()).isNull();
        assertThat(venda.getTroco()).isNull();

        assertThat(estoqueService.consultarSaldo(EAN_ISENTO).getSaldoDisponivel()).isEqualTo(8);
        assertThat(estoqueService.listarMovimentacoes(EAN_ISENTO, null))
                .filteredOn(m -> m.getTipo().equals("SAIDA_VENDA"))
                .allSatisfy(m -> assertThat(m.getObservacao()).isEqualTo("Venda nº " + venda.getId()))
                .hasSize(2);
    }

    @Test
    void precoCobradoNaoMudaQuandoOPrecoDoProdutoEAlteradoDepois() {
        Long idVenda = vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_GERAL, 1))).getId();
        cadastrarPreco(EAN_GERAL, "7.00");

        assertThat(vendaService.consultarVenda(idVenda).getItens().getFirst().getPrecoUnitario()).isEqualByComparingTo("5.00");
    }

    @Test
    void pagamentoEmDinheiroCalculaTrocoEAplicaDesconto() {
        VendaOutputDTO venda = vendaService.registrarVenda(venda(FormaPagamento.DINHEIRO, "0.50", "20.00", item(EAN_GERAL, 2)));

        assertThat(venda.getValorBruto()).isEqualByComparingTo("10.00");
        assertThat(venda.getDesconto()).isEqualByComparingTo("0.50");
        assertThat(venda.getValorTotal()).isEqualByComparingTo("9.50");
        assertThat(venda.getValorRecebido()).isEqualByComparingTo("20.00");
        assertThat(venda.getTroco()).isEqualByComparingTo("10.50");
    }

    @Test
    void pagamentoEmDinheiroExigeValorRecebidoSuficiente() {
        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.DINHEIRO, null, null, item(EAN_GERAL, 2))),
                HttpStatus.BAD_REQUEST, "Informe o valor recebido");
        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.DINHEIRO, null, "9.99", item(EAN_GERAL, 2))),
                HttpStatus.BAD_REQUEST, "menor que o total");
    }

    @Test
    void descontoNaoPodePassarDoValorDaVenda() {
        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.PIX, "10.01", null, item(EAN_GERAL, 2))),
                HttpStatus.BAD_REQUEST, "não pode ser maior que o valor da venda");
    }

    @Test
    void descontoIgualAoValorDaVendaZeraOTotal() {
        assertThat(vendaService.registrarVenda(venda(FormaPagamento.PIX, "10.00", null, item(EAN_GERAL, 2))).getValorTotal())
                .isEqualByComparingTo("0");
    }

    @Test
    void produtoSemPrecoNaoPodeSerVendido() {
        long vendasAntes = vendaRepository.count();
        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_SEM_PRECO, 1))),
                HttpStatus.CONFLICT, "não tem preço cadastrado");
        assertThat(vendaRepository.count()).isEqualTo(vendasAntes);
    }

    @Test
    void estoqueInsuficienteEmUmItemNaoGravaNenhumaParteDaVenda() {
        long vendasAntes = vendaRepository.count();

        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_GERAL, 2), item(EAN_ISENTO, 50))),
                HttpStatus.CONFLICT, "Estoque insuficiente para Dipirona 1g: solicitado 50, disponível 13");

        assertThat(vendaRepository.count()).isEqualTo(vendasAntes);
        assertThat(estoqueService.consultarSaldo(EAN_GERAL).getSaldoDisponivel()).isEqualTo(20);
    }

    @Test
    void mesmoProdutoDuasVezesNaVendaERecusado() {
        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_GERAL, 1), item(EAN_GERAL, 2))),
                HttpStatus.BAD_REQUEST, "aparece mais de uma vez");
    }

    @Test
    void produtoInexistenteRetorna404() {
        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item("0000000000000", 1))),
                HttpStatus.NOT_FOUND, "Produto não encontrado");
    }

    // ---------------------------------------------------------- receitas

    @Test
    void remedioSobPrescricaoExigeConfirmacaoDaReceita() {
        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_PRESCRICAO, 1))),
                HttpStatus.BAD_REQUEST, "exige receita");

        ItemVendaInputDTO comReceita = item(EAN_PRESCRICAO, 1);
        comReceita.setReceitaApresentada(true);
        ItemVendaOutputDTO vendido = vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, comReceita)).getItens().getFirst();

        assertThat(vendido.getTipoControle()).isEqualTo("sob_prescricao");
        assertThat(vendido.getReceitaApresentada()).isTrue();
        assertThat(vendido.getReceita()).isNull();
    }

    @Test
    void remedioDeControleEspecialExigeOsDadosCompletosDaReceita() {
        ItemVendaInputDTO incompleto = item(EAN_ESPECIAL, 1);
        incompleto.setReceitaApresentada(true);
        incompleto.setReceita(receita("Maria da Silva", "Dr. João Souza", "CRM-AM 12345", " "));
        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, incompleto)),
                HttpStatus.BAD_REQUEST, "controle especial");

        ItemVendaInputDTO completo = item(EAN_ESPECIAL, 1);
        completo.setReceitaApresentada(true);
        completo.setReceita(receita("Maria da Silva", "Dr. João Souza", "CRM-AM 12345", "AM-0012345"));
        ItemVendaOutputDTO vendido = vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, completo)).getItens().getFirst();

        assertThat(vendido.getTipoControle()).isEqualTo("especial");
        assertThat(vendido.getReceita().getNomePaciente()).isEqualTo("Maria da Silva");
        assertThat(vendido.getReceita().getRegistroPrescritor()).isEqualTo("CRM-AM 12345");
        assertThat(vendido.getReceita().getNumeroReceita()).isEqualTo("AM-0012345");
    }

    @Test
    void remedioDeControleEspecialTambemExigeConfirmacao() {
        ItemVendaInputDTO semConfirmacao = item(EAN_ESPECIAL, 1);
        semConfirmacao.setReceita(receita("Maria da Silva", "Dr. João Souza", "CRM-AM 12345", "AM-0012345"));
        assertErro(() -> vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, semConfirmacao)),
                HttpStatus.BAD_REQUEST, "exige receita");
    }

    @Test
    void dadosDeReceitaSaoIgnoradosEmItensQueNaoExigemReceita() {
        ItemVendaInputDTO geral = item(EAN_GERAL, 1);
        geral.setReceitaApresentada(true);
        geral.setReceita(receita("Maria", "Dr. João", "CRM-AM 1", "1"));

        ItemVendaOutputDTO vendido = vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, geral)).getItens().getFirst();

        assertThat(vendido.getTipoControle()).isNull();
        assertThat(vendido.getReceitaApresentada()).isNull();
        assertThat(vendido.getReceita()).isNull();
    }

    // ---------------------------------------------------------- cancelamento

    @Test
    void cancelamentoDevolveCadaItemAoLoteDeOrigem() {
        Long idVenda = vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_ISENTO, 5))).getId();

        VendaOutputDTO cancelada = vendaService.cancelarVenda(idVenda, "  Cliente desistiu  ");

        assertThat(cancelada.getStatus()).isEqualTo("CANCELADA");
        assertThat(cancelada.getMotivoCancelamento()).isEqualTo("Cliente desistiu");
        assertThat(cancelada.getDataCancelamento()).isNotNull();
        assertThat(estoqueService.consultarSaldo(EAN_ISENTO).getLotes())
                .extracting(l -> l.getCodigoLote(), l -> l.getQuantidadeAtual())
                .containsExactly(Tuple.tuple("CEDO", 3), Tuple.tuple("TARDE", 10));
        assertThat(estoqueService.listarMovimentacoes(EAN_ISENTO, null))
                .filteredOn(m -> m.getTipo().equals("CANCELAMENTO_VENDA"))
                .hasSize(2)
                .allSatisfy(m -> assertThat(m.getObservacao()).isEqualTo("Cancelamento da venda nº " + idVenda + ": Cliente desistiu"));
    }

    @Test
    void vendaJaCanceladaNaoPodeSerCanceladaDeNovo() {
        Long idVenda = vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_GERAL, 1))).getId();
        vendaService.cancelarVenda(idVenda, "Erro de digitação");

        assertErro(() -> vendaService.cancelarVenda(idVenda, "De novo"), HttpStatus.CONFLICT, "já foi cancelada");
        assertThat(estoqueService.consultarSaldo(EAN_GERAL).getSaldoDisponivel()).isEqualTo(20);
    }

    // ---------------------------------------------------------- consultas

    @Test
    void consultaDeVendaInexistenteRetorna404() {
        assertErro(() -> vendaService.consultarVenda(999999L), HttpStatus.NOT_FOUND, "Venda não encontrada");
        assertErro(() -> vendaService.cancelarVenda(999999L, "x"), HttpStatus.NOT_FOUND, "Venda não encontrada");
    }

    @Test
    void listaVendasPorPeriodo() {
        Long idVenda = vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_GERAL, 1))).getId();

        assertThat(vendaService.listarVendas(null, null)).extracting(VendaOutputDTO::getId).contains(idVenda);
        assertThat(vendaService.listarVendas(hoje.minusDays(7), hoje)).extracting(VendaOutputDTO::getId).contains(idVenda);
        assertThat(vendaService.listarVendas(hoje.minusDays(7), hoje.minusDays(1))).isEmpty();
        assertErro(() -> vendaService.listarVendas(hoje, hoje.minusDays(1)), HttpStatus.BAD_REQUEST, "data final");
    }

    @Test
    void resumoDoDiaConsideraApenasVendasConcluidas() {
        vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_ISENTO, 5)));                // 64,50
        vendaService.registrarVenda(venda(FormaPagamento.DINHEIRO, "0.50", "20.00", item(EAN_GERAL, 2)));       // 9,50
        Long cancelada = vendaService.registrarVenda(venda(FormaPagamento.PIX, null, null, item(EAN_GERAL, 1))).getId();
        vendaService.cancelarVenda(cancelada, "Teste");

        ResumoVendasOutputDTO resumo = vendaService.resumoDoDia(null);

        assertThat(resumo.getData()).isEqualTo(hoje);
        assertThat(resumo.getQuantidadeVendas()).isEqualTo(2);
        assertThat(resumo.getQuantidadeCanceladas()).isEqualTo(1);
        assertThat(resumo.getValorBruto()).isEqualByComparingTo("74.50");
        assertThat(resumo.getDescontos()).isEqualByComparingTo("0.50");
        assertThat(resumo.getValorTotal()).isEqualByComparingTo("74.00");
        assertThat(resumo.getTicketMedio()).isEqualByComparingTo("37.00");
        assertThat(resumo.getPorFormaPagamento())
                .extracting(ResumoVendasOutputDTO.TotalPorFormaPagamento::formaPagamento,
                        ResumoVendasOutputDTO.TotalPorFormaPagamento::quantidadeVendas,
                        t -> t.valorTotal().toPlainString())
                .containsExactly(
                        Tuple.tuple("DINHEIRO", 1L, "9.50"),
                        Tuple.tuple("CARTAO_DEBITO", 0L, "0.00"),
                        Tuple.tuple("CARTAO_CREDITO", 0L, "0.00"),
                        Tuple.tuple("PIX", 1L, "64.50"));
        assertThat(vendaService.resumoDoDia(hoje.minusDays(1)).getQuantidadeVendas()).isZero();
    }

    @Test
    void consultaDeProdutoParaOCaixaTrazPrecoSaldoEExigenciaDeReceita() {
        ProdutoPdvOutputDTO especial = vendaService.consultarProdutoParaVenda(EAN_ESPECIAL);
        assertThat(especial.getPrecoAtual()).isEqualByComparingTo("45.00");
        assertThat(especial.getSaldoDisponivel()).isEqualTo(5);
        assertThat(especial.getTipoControle()).isEqualTo("especial");
        assertThat(especial.isExigeReceita()).isTrue();
        assertThat(especial.isExigeDadosReceita()).isTrue();

        ProdutoPdvOutputDTO prescricao = vendaService.consultarProdutoParaVenda(EAN_PRESCRICAO);
        assertThat(prescricao.isExigeReceita()).isTrue();
        assertThat(prescricao.isExigeDadosReceita()).isFalse();

        ProdutoPdvOutputDTO geral = vendaService.consultarProdutoParaVenda(EAN_GERAL);
        assertThat(geral.getTipoProduto()).isEqualTo("geral");
        assertThat(geral.getTipoControle()).isNull();
        assertThat(geral.isExigeReceita()).isFalse();

        assertThat(vendaService.consultarProdutoParaVenda(EAN_SEM_PRECO).getPrecoAtual()).isNull();
    }

    // ---------------------------------------------------------- auxiliares

    private void criarRemedio(String ean, String nome, String tipoControle) {
        Remedio remedio = new Remedio();
        remedio.setEan(ean);
        remedio.setNomeComercial(nome);
        remedio.setUnidadeDeMedida(medidaPadraoRepository.findBySigla("CX"));
        remedio.setControle(controleReceitaRepository.findByTipoControle(tipoControle));
        produtoRepository.save(remedio);
    }

    private void cadastrarPreco(String ean, String valor) {
        PrecoInputDTO dto = new PrecoInputDTO();
        dto.setEan(ean);
        dto.setValorVenda(new BigDecimal(valor));
        precoService.create(dto);
    }

    private void darEntrada(String ean, String codigoLote, LocalDate validade, int quantidade) {
        EntradaEstoqueInputDTO dto = new EntradaEstoqueInputDTO();
        dto.setEan(ean);
        dto.setCodigoLote(codigoLote);
        dto.setDataValidade(validade);
        dto.setQuantidade(quantidade);
        dto.setCustoUnitario(new BigDecimal("1.00"));
        dto.setFornecedor("Distribuidora Teste");
        estoqueService.registrarEntrada(dto);
    }

    private ItemVendaInputDTO item(String ean, int quantidade) {
        ItemVendaInputDTO dto = new ItemVendaInputDTO();
        dto.setEan(ean);
        dto.setQuantidade(quantidade);
        return dto;
    }

    private VendaInputDTO venda(FormaPagamento forma, String desconto, String valorRecebido, ItemVendaInputDTO... itens) {
        VendaInputDTO dto = new VendaInputDTO();
        dto.setItens(List.of(itens));
        dto.setFormaPagamento(forma);
        dto.setDesconto(desconto == null ? null : new BigDecimal(desconto));
        dto.setValorRecebido(valorRecebido == null ? null : new BigDecimal(valorRecebido));
        return dto;
    }

    private DadosReceitaDTO receita(String paciente, String prescritor, String registro, String numero) {
        DadosReceitaDTO dto = new DadosReceitaDTO();
        dto.setNomePaciente(paciente);
        dto.setNomePrescritor(prescritor);
        dto.setRegistroPrescritor(registro);
        dto.setNumeroReceita(numero);
        return dto;
    }

    private void assertErro(org.assertj.core.api.ThrowableAssert.ThrowingCallable chamada, HttpStatus status, String trechoDaMensagem) {
        assertThatThrownBy(chamada)
                .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(status);
                    assertThat(e.getReason()).contains(trechoDaMensagem);
                });
    }
}
