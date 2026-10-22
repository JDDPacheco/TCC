package com.josepacheco.tcc.service.estoque;

import com.josepacheco.tcc.dto.estoque.AjusteEstoqueInputDTO;
import com.josepacheco.tcc.dto.estoque.EntradaEstoqueInputDTO;
import com.josepacheco.tcc.dto.estoque.LoteOutputDTO;
import com.josepacheco.tcc.dto.estoque.MovimentacaoEstoqueInputDTO;
import com.josepacheco.tcc.dto.estoque.MovimentacaoEstoqueOutputDTO;
import com.josepacheco.tcc.dto.estoque.SaldoProdutoOutputDTO;
import com.josepacheco.tcc.model.estoque.Lote;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.repository.estoque.LoteRepository;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
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
 * Testes das regras do estoque sobre um banco H2 em memória.
 * Cada teste roda em uma transação desfeita ao final, então um não interfere no outro.
 */
@SpringBootTest
@Transactional
class EstoqueServiceTest {

    private static final String EAN_REMEDIO = "7890000000001";
    private static final String EAN_GERAL = "7890000000002";

    @Autowired
    private EstoqueService estoqueService;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MedidaPadraoRepository medidaPadraoRepository;
    @Autowired
    private LoteRepository loteRepository;

    private final LocalDate hoje = LocalDate.now();

    @BeforeEach
    void cadastrarProdutos() {
        Remedio remedio = new Remedio();
        remedio.setEan(EAN_REMEDIO);
        remedio.setNomeComercial("Dipirona Teste 1g");
        remedio.setUnidadeDeMedida(medidaPadraoRepository.findBySigla("CX"));
        produtoRepository.save(remedio);

        produtoRepository.save(new Produto("Escova Dental Teste", EAN_GERAL, medidaPadraoRepository.findBySigla("UN")));
    }

    // ---------------------------------------------------------- entrada

    @Test
    void entradaDeRemedioCriaLoteERegistraMovimentacaoComCustoEFornecedor() {
        MovimentacaoEstoqueOutputDTO mov = estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 10));

        assertThat(mov.getTipo()).isEqualTo("ENTRADA");
        assertThat(mov.getQuantidade()).isEqualTo(10);
        assertThat(mov.getSaldoApos()).isEqualTo(10);
        assertThat(mov.getCustoUnitario()).isEqualByComparingTo("7.50");
        assertThat(mov.getFornecedor()).isEqualTo("Distribuidora Teste");
        assertThat(estoqueService.consultarSaldo(EAN_REMEDIO).getSaldoDisponivel()).isEqualTo(10);
    }

    @Test
    void entradaNoMesmoLoteSomaAoSaldo() {
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 10));
        MovimentacaoEstoqueOutputDTO segunda = estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 5));

        assertThat(segunda.getSaldoApos()).isEqualTo(15);
        assertThat(estoqueService.consultarSaldo(EAN_REMEDIO).getLotes()).hasSize(1);
    }

    @Test
    void entradaDeRemedioExigeCodigoDoLoteEValidade() {
        assertErro(() -> estoqueService.registrarEntrada(entrada(EAN_REMEDIO, " ", hoje.plusMonths(6), 10)),
                HttpStatus.BAD_REQUEST, "código do lote");
        assertErro(() -> estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", null, 10)),
                HttpStatus.BAD_REQUEST, "validade");
    }

    @Test
    void entradaRecusaLoteVencido() {
        assertErro(() -> estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.minusDays(1), 10)),
                HttpStatus.BAD_REQUEST, "vencido");
    }

    @Test
    void entradaRecusaLoteExistenteComOutraValidade() {
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 10));
        assertErro(() -> estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(7), 5)),
                HttpStatus.BAD_REQUEST, "já está cadastrado");
    }

    @Test
    void entradaDeProdutoInexistenteRetorna404() {
        assertErro(() -> estoqueService.registrarEntrada(entrada("0000000000000", "L1", hoje.plusMonths(6), 10)),
                HttpStatus.NOT_FOUND, "Produto não encontrado");
    }

    @Test
    void produtoGeralUsaLoteUnicoSemValidadeIgnorandoLoteInformado() {
        estoqueService.registrarEntrada(entrada(EAN_GERAL, "QUALQUER", hoje.plusYears(1), 30));
        estoqueService.registrarEntrada(entrada(EAN_GERAL, null, null, 20));

        SaldoProdutoOutputDTO saldo = estoqueService.consultarSaldo(EAN_GERAL);
        assertThat(saldo.getTipoProduto()).isEqualTo("geral");
        assertThat(saldo.getSaldoDisponivel()).isEqualTo(50);
        assertThat(saldo.getLotes()).singleElement().satisfies(lote -> {
            assertThat(lote.getCodigoLote()).isEqualTo(EstoqueService.CODIGO_LOTE_UNICO);
            assertThat(lote.getDataValidade()).isNull();
            assertThat(lote.getSituacao()).isEqualTo("SEM_VALIDADE");
        });
    }

    @Test
    void mesmoCodigoDeLotePodeExistirEmProdutosDiferentes() {
        Remedio outro = new Remedio();
        outro.setEan("7890000000003");
        outro.setNomeComercial("Paracetamol Teste");
        outro.setUnidadeDeMedida(medidaPadraoRepository.findBySigla("CX"));
        produtoRepository.save(outro);

        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "ABC123", hoje.plusMonths(6), 10));
        estoqueService.registrarEntrada(entrada("7890000000003", "ABC123", hoje.plusMonths(8), 4));

        assertThat(estoqueService.consultarSaldo("7890000000003").getSaldoDisponivel()).isEqualTo(4);
        assertThat(estoqueService.consultarSaldo(EAN_REMEDIO).getSaldoDisponivel()).isEqualTo(10);
    }

    // ---------------------------------------------------------- baixa FEFO

    @Test
    void baixaConsomePrimeiroOLoteQueVenceAntesEPulaVencidos() {
        Lote vencido = criarLoteDireto("VENCIDO", hoje.minusDays(5), 50);
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "TARDE", hoje.plusMonths(10), 10));
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "CEDO", hoje.plusMonths(2), 3));

        List<LoteBaixado> baixas = estoqueService.baixarEstoque(EAN_REMEDIO, 5, "Venda teste");

        assertThat(baixas).extracting(b -> b.lote().getCodigoLote(), LoteBaixado::quantidade)
                .containsExactly(org.assertj.core.groups.Tuple.tuple("CEDO", 3), org.assertj.core.groups.Tuple.tuple("TARDE", 2));
        assertThat(vencido.getQuantidadeAtual()).isEqualTo(50);
        assertThat(estoqueService.consultarSaldo(EAN_REMEDIO).getSaldoDisponivel()).isEqualTo(8);
        assertThat(estoqueService.listarMovimentacoes(EAN_REMEDIO, null))
                .filteredOn(m -> m.getTipo().equals("SAIDA_VENDA")).hasSize(2);
    }

    @Test
    void baixaComEstoqueInsuficienteNaoAlteraSaldos() {
        criarLoteDireto("VENCIDO", hoje.minusDays(5), 50);
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(2), 3));

        assertErro(() -> estoqueService.baixarEstoque(EAN_REMEDIO, 4, "Venda teste"), HttpStatus.CONFLICT, "disponível 3");
        assertThat(estoqueService.consultarSaldo(EAN_REMEDIO).getSaldoDisponivel()).isEqualTo(3);
    }

    @Test
    void baixaRecusaQuantidadeZero() {
        assertErro(() -> estoqueService.baixarEstoque(EAN_REMEDIO, 0, "Venda teste"), HttpStatus.BAD_REQUEST, "maior que zero");
    }

    // ---------------------------------------------------------- ajuste, perda e devolução

    @Test
    void ajusteRegistraADiferencaEntreContadoESaldo() {
        Long idLote = estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 10)).getIdLote();

        MovimentacaoEstoqueOutputDTO paraBaixo = estoqueService.registrarAjuste(ajuste(idLote, 7));
        assertThat(paraBaixo.getQuantidade()).isEqualTo(-3);
        assertThat(paraBaixo.getSaldoApos()).isEqualTo(7);

        MovimentacaoEstoqueOutputDTO paraCima = estoqueService.registrarAjuste(ajuste(idLote, 9));
        assertThat(paraCima.getQuantidade()).isEqualTo(2);
        assertThat(paraCima.getSaldoApos()).isEqualTo(9);
    }

    @Test
    void ajusteComQuantidadeIgualAoSaldoFalha() {
        Long idLote = estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 10)).getIdLote();
        assertErro(() -> estoqueService.registrarAjuste(ajuste(idLote, 10)), HttpStatus.BAD_REQUEST, "não há o que ajustar");
    }

    @Test
    void perdaRetiraDoLoteEFalhaSeMaiorQueOSaldo() {
        Long idLote = estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 10)).getIdLote();

        assertThat(estoqueService.registrarPerda(movimentacao(idLote, 4)).getSaldoApos()).isEqualTo(6);
        assertErro(() -> estoqueService.registrarPerda(movimentacao(idLote, 7)), HttpStatus.CONFLICT, "maior que o saldo");
    }

    @Test
    void perdaPodeZerarLoteVencido() {
        Lote vencido = criarLoteDireto("VENCIDO", hoje.minusDays(5), 6);
        assertThat(estoqueService.registrarPerda(movimentacao(vencido.getId(), 6)).getSaldoApos()).isZero();
        assertThat(estoqueService.listarLotesVencidos()).isEmpty();
    }

    @Test
    void devolucaoSomaAoLote() {
        Long idLote = estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 10)).getIdLote();
        MovimentacaoEstoqueOutputDTO devolucao = estoqueService.registrarDevolucao(movimentacao(idLote, 2));

        assertThat(devolucao.getTipo()).isEqualTo("DEVOLUCAO");
        assertThat(devolucao.getSaldoApos()).isEqualTo(12);
    }

    @Test
    void movimentacaoEmLoteInexistenteRetorna404() {
        assertErro(() -> estoqueService.registrarPerda(movimentacao(999999L, 1)), HttpStatus.NOT_FOUND, "Lote não encontrado");
    }

    // ---------------------------------------------------------- consultas e alertas

    @Test
    void saldoSeparaDisponivelDeVencidoEMarcaSituacaoDosLotes() {
        criarLoteDireto("VENCIDO", hoje.minusDays(1), 2);
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "PROXIMO", hoje.plusDays(10), 3));
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "LONGE", hoje.plusMonths(6), 5));

        SaldoProdutoOutputDTO saldo = estoqueService.consultarSaldo(EAN_REMEDIO);
        assertThat(saldo.getSaldoDisponivel()).isEqualTo(8);
        assertThat(saldo.getSaldoVencido()).isEqualTo(2);
        assertThat(saldo.getLotes()).extracting(LoteOutputDTO::getCodigoLote, LoteOutputDTO::getSituacao)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("VENCIDO", "VENCIDO"),
                        org.assertj.core.groups.Tuple.tuple("PROXIMO", "A_VENCER"),
                        org.assertj.core.groups.Tuple.tuple("LONGE", "OK"));
    }

    @Test
    void alertasDeVencimentoListamApenasLotesComSaldoNoPeriodo() {
        criarLoteDireto("VENCIDO", hoje.minusDays(1), 2);
        criarLoteDireto("VENCIDO_ZERADO", hoje.minusDays(3), 0);
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "PROXIMO", hoje.plusDays(10), 3));
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "LONGE", hoje.plusDays(60), 5));

        assertThat(estoqueService.listarLotesAVencer(30)).extracting(LoteOutputDTO::getCodigoLote).containsExactly("PROXIMO");
        assertThat(estoqueService.listarLotesAVencer(90)).extracting(LoteOutputDTO::getCodigoLote).containsExactly("PROXIMO", "LONGE");
        assertThat(estoqueService.listarLotesVencidos()).extracting(LoteOutputDTO::getCodigoLote).containsExactly("VENCIDO");
        assertErro(() -> estoqueService.listarLotesAVencer(-1), HttpStatus.BAD_REQUEST, "negativo");
    }

    @Test
    void estoqueBaixoConsideraMinimoESaldoDisponivel() {
        estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 5));
        estoqueService.registrarEntrada(entrada(EAN_GERAL, null, null, 50));

        assertThat(estoqueService.listarEstoqueBaixo()).isEmpty(); // sem mínimo configurado, nada entra no alerta

        estoqueService.definirEstoqueMinimo(EAN_REMEDIO, 5);
        estoqueService.definirEstoqueMinimo(EAN_GERAL, 10);
        assertThat(estoqueService.listarEstoqueBaixo()).extracting(SaldoProdutoOutputDTO::getEan).containsExactly(EAN_REMEDIO);

        estoqueService.definirEstoqueMinimo(EAN_REMEDIO, null);
        assertThat(estoqueService.listarEstoqueBaixo()).isEmpty();
    }

    @Test
    void historicoDeMovimentacoesVemDoMaisRecenteParaOMaisAntigo() {
        Long idLote = estoqueService.registrarEntrada(entrada(EAN_REMEDIO, "L1", hoje.plusMonths(6), 10)).getIdLote();
        estoqueService.registrarPerda(movimentacao(idLote, 1));
        estoqueService.registrarDevolucao(movimentacao(idLote, 1));

        assertThat(estoqueService.listarMovimentacoes(null, idLote)).extracting(MovimentacaoEstoqueOutputDTO::getTipo)
                .containsExactly("DEVOLUCAO", "PERDA", "ENTRADA");
        assertThat(estoqueService.listarMovimentacoes(EAN_REMEDIO, null)).hasSize(3);
        assertThat(estoqueService.listarMovimentacoes(EAN_GERAL, null)).isEmpty();
    }

    // ---------------------------------------------------------- auxiliares

    private EntradaEstoqueInputDTO entrada(String ean, String codigoLote, LocalDate validade, int quantidade) {
        EntradaEstoqueInputDTO dto = new EntradaEstoqueInputDTO();
        dto.setEan(ean);
        dto.setCodigoLote(codigoLote);
        dto.setDataValidade(validade);
        dto.setQuantidade(quantidade);
        dto.setCustoUnitario(new BigDecimal("7.50"));
        dto.setFornecedor("Distribuidora Teste");
        return dto;
    }

    private AjusteEstoqueInputDTO ajuste(Long idLote, int quantidadeContada) {
        AjusteEstoqueInputDTO dto = new AjusteEstoqueInputDTO();
        dto.setIdLote(idLote);
        dto.setQuantidadeContada(quantidadeContada);
        dto.setObservacao("Inventário de teste");
        return dto;
    }

    private MovimentacaoEstoqueInputDTO movimentacao(Long idLote, int quantidade) {
        MovimentacaoEstoqueInputDTO dto = new MovimentacaoEstoqueInputDTO();
        dto.setIdLote(idLote);
        dto.setQuantidade(quantidade);
        dto.setObservacao("Motivo de teste");
        return dto;
    }

    // A entrada pelo service recusa lotes vencidos; para simular um lote que venceu depois de entrar, ele é gravado direto
    private Lote criarLoteDireto(String codigo, LocalDate validade, int quantidade) {
        Lote lote = new Lote(produtoRepository.findByEan(EAN_REMEDIO).orElseThrow(), codigo, validade);
        lote.setQuantidadeAtual(quantidade);
        return loteRepository.save(lote);
    }

    private void assertErro(org.assertj.core.api.ThrowableAssert.ThrowingCallable chamada, HttpStatus status, String trechoDaMensagem) {
        assertThatThrownBy(chamada)
                .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(status);
                    assertThat(e.getReason()).contains(trechoDaMensagem);
                });
    }
}
