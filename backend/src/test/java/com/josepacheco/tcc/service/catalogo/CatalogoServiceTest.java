package com.josepacheco.tcc.service.catalogo;

import com.josepacheco.tcc.dto.catalogo.ItemCatalogoOutputDTO;
import com.josepacheco.tcc.dto.estoque.EntradaEstoqueInputDTO;
import com.josepacheco.tcc.dto.preco.PrecoInputDTO;
import com.josepacheco.tcc.model.estoque.Lote;
import com.josepacheco.tcc.model.preco.Preco;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.repository.estoque.LoteRepository;
import com.josepacheco.tcc.repository.preco.PrecoRepository;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ControleReceitaRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.LaboratorioRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.FormulaRepository;
import com.josepacheco.tcc.service.estoque.EstoqueService;
import com.josepacheco.tcc.service.preco.PrecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Catálogo da tela inicial: junta marca, fórmula, saldo disponível e preço atual de cada produto.
 */
@SpringBootTest
@Transactional
class CatalogoServiceTest {

    private static final String EAN_REMEDIO = "7890000000701";
    private static final String EAN_GERAL = "7890000000702";

    @Autowired
    private CatalogoService catalogoService;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MedidaPadraoRepository medidaPadraoRepository;
    @Autowired
    private FormulaRepository formulaRepository;
    @Autowired
    private LaboratorioRepository laboratorioRepository;
    @Autowired
    private ControleReceitaRepository controleReceitaRepository;
    @Autowired
    private PrecoService precoService;
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private EstoqueService estoqueService;
    @Autowired
    private LoteRepository loteRepository;

    @BeforeEach
    void cadastrarProdutos() {
        Remedio remedio = new Remedio();
        remedio.setEan(EAN_REMEDIO);
        remedio.setNomeComercial("Dipirona Monoidratada 1g");
        remedio.setUnidadeDeMedida(medidaPadraoRepository.findBySigla("CX"));
        remedio.setFormula(formulaRepository.findAll().getFirst());          // "dipirona monoidratada 1 g"
        remedio.setLaboratorio(laboratorioRepository.findAll().getFirst());  // "Prati-Donaduzzi"
        remedio.setControle(controleReceitaRepository.findByTipoControle("isento"));
        produtoRepository.save(remedio);

        produtoRepository.save(new Produto("escova dental", EAN_GERAL, medidaPadraoRepository.findBySigla("UN")));
    }

    @Test
    void remedioTrazMarcaFormulaPrecoAtualESaldoDisponivel() {
        Long precoAntigo = cadastrarPreco(EAN_REMEDIO, "11.00");
        voltarVigencia(precoAntigo);
        cadastrarPreco(EAN_REMEDIO, "12.90");

        darEntrada(EAN_REMEDIO, "L1", LocalDate.now().plusMonths(6), 10);
        Lote vencido = new Lote(produtoRepository.findByEan(EAN_REMEDIO).orElseThrow(), "VENCIDO", LocalDate.now().minusDays(1));
        vencido.setQuantidadeAtual(4);
        loteRepository.save(vencido);

        ItemCatalogoOutputDTO item = itemDoCatalogo(EAN_REMEDIO);

        assertThat(item.getNomeComercial()).isEqualTo("Dipirona Monoidratada 1g");
        assertThat(item.getTipoProduto()).isEqualTo("remedio");
        assertThat(item.getMarca()).isEqualTo("Prati-Donaduzzi");
        assertThat(item.getFormula()).isEqualTo("dipirona monoidratada 1 g");
        assertThat(item.getTipoControle()).isEqualTo("isento");
        assertThat(item.getUnidadeDeMedida()).isEqualTo("CX");
        assertThat(item.getPrecoAtual()).isEqualByComparingTo("12.90");
        assertThat(item.getSaldoDisponivel()).isEqualTo(10); // o lote vencido não conta
    }

    @Test
    void produtoGeralSemPrecoNemEstoqueApareceComCamposVazios() {
        ItemCatalogoOutputDTO item = itemDoCatalogo(EAN_GERAL);

        assertThat(item.getTipoProduto()).isEqualTo("geral");
        assertThat(item.getMarca()).isNull();
        assertThat(item.getFormula()).isNull();
        assertThat(item.getTipoControle()).isNull();
        assertThat(item.getPrecoAtual()).isNull();
        assertThat(item.getSaldoDisponivel()).isZero();
    }

    @Test
    void produtosVemEmOrdemAlfabeticaSemDiferenciarMaiusculas() {
        assertThat(catalogoService.listar()).extracting(ItemCatalogoOutputDTO::getEan).containsExactly(EAN_REMEDIO, EAN_GERAL);
    }

    // ---------------------------------------------------------- auxiliares

    private ItemCatalogoOutputDTO itemDoCatalogo(String ean) {
        return catalogoService.listar().stream().filter(i -> i.getEan().equals(ean)).findFirst().orElseThrow();
    }

    private Long cadastrarPreco(String ean, String valor) {
        PrecoInputDTO dto = new PrecoInputDTO();
        dto.setEan(ean);
        dto.setValorVenda(new BigDecimal(valor));
        return precoService.create(dto).getId();
    }

    // Registros criados no mesmo instante teriam a mesma data; o preço antigo é recuado no tempo
    private void voltarVigencia(Long idPreco) {
        Preco preco = precoRepository.findById(idPreco).orElseThrow();
        preco.setDataInicioVigencia(preco.getDataInicioVigencia().minusDays(1));
        precoRepository.saveAndFlush(preco);
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
}
