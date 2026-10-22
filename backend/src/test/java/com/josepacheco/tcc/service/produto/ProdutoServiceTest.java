package com.josepacheco.tcc.service.produto;

import com.josepacheco.tcc.dto.produto.ProdutoDTO;
import com.josepacheco.tcc.dto.produto.remedio.GenericoInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.GenericoOutputDTO;
import com.josepacheco.tcc.dto.produto.remedio.RemedioInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.RemedioOutputDTO;
import com.josepacheco.tcc.model.produto.remedio.Generico;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.model.produto.remedio.atributos.Laboratorio;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.LaboratorioRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.FormulaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Regras do cadastro de produtos (geral, remédio e genérico) sobre H2 em memória.
 * Usa a fórmula e o laboratório de exemplo criados pelo DataLoader.
 */
@SpringBootTest
@Transactional
class ProdutoServiceTest {

    @Autowired
    private ProdutoService produtoService;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private FormulaRepository formulaRepository;
    @Autowired
    private LaboratorioRepository laboratorioRepository;

    private Long idFormula;
    private Long idLaboratorio;

    @BeforeEach
    void carregarDadosDeApoio() {
        idFormula = formulaRepository.findAll().getFirst().getId();         // "dipirona monoidratada 1 g"
        idLaboratorio = laboratorioRepository.findAll().getFirst().getId(); // "Prati-Donaduzzi"
    }

    // ---------------------------------------------------------- cadastro

    @Test
    void cadastraProdutoGeral() {
        ProdutoDTO criado = produtoService.create(produtoGeral("7890000000101", "Escova Dental", "UN"));

        assertThat(criado.getTipoProduto()).isEqualTo("geral");
        assertThat(criado.getNomeComercial()).isEqualTo("Escova Dental");
        assertThat(criado.getUnidadeDeMedida()).isEqualTo("UN");
        assertThat(produtoService.list()).extracting(ProdutoDTO::getEan).contains("7890000000101");
    }

    @Test
    void cadastraRemedioResolvendoAsDependencias() {
        ProdutoDTO criado = produtoService.create(remedio(new RemedioInputDTO(), "7890000000102", "Dipirona 1g"));

        assertThat(criado).isInstanceOf(RemedioOutputDTO.class);
        RemedioOutputDTO remedio = (RemedioOutputDTO) criado;
        assertThat(remedio.getTipoProduto()).isEqualTo("remedio");
        assertThat(remedio.getFormula()).isEqualTo("dipirona monoidratada 1 g");
        assertThat(remedio.getNomeLaboratorio()).isEqualTo("Prati-Donaduzzi");
        assertThat(remedio.getApresentacao()).isEqualTo("Comprimido");
        assertThat(remedio.getDescricaoMedidaDoses()).isEqualTo("Comprimido");
        assertThat(remedio.getTipoControle()).isEqualTo("isento");
        assertThat(remedio.getQuantidadeDoses()).isEqualTo(10f);
    }

    @Test
    void pesoLiquidoDoRemedioESempreEmGramas() {
        RemedioInputDTO dto = remedio(new RemedioInputDTO(), "7890000000103", "Dipirona Pó");
        dto.setPesoLiquido(50);

        RemedioOutputDTO criado = (RemedioOutputDTO) produtoService.create(dto);

        assertThat(criado.getPesoLiquido()).isEqualTo(50f);
        assertThat(criado.getUnidadeMedidaPesoLiquido()).isEqualTo("grama");
    }

    @Test
    void cadastraGenericoComoSubtipoDeRemedio() {
        ProdutoDTO criado = produtoService.create(remedio(new GenericoInputDTO(), "7890000000104", "Dipirona Genérico"));

        assertThat(criado).isInstanceOf(GenericoOutputDTO.class);
        assertThat(criado.getTipoProduto()).isEqualTo("generico");
        assertThat(produtoRepository.findByEan("7890000000104")).get().isInstanceOf(Generico.class);
    }

    @Test
    void recusaEanDuplicado() {
        produtoService.create(produtoGeral("7890000000105", "Produto A", "UN"));

        assertErro(() -> produtoService.create(produtoGeral("7890000000105", "Produto B", "UN")),
                HttpStatus.CONFLICT, "Já existe um produto com o EAN");
    }

    @Test
    void consultaPorEan() {
        produtoService.create(produtoGeral("7890000000106", "Sabonete", "UN"));

        assertThat(produtoService.getByEan("7890000000106").getNomeComercial()).isEqualTo("Sabonete");
        assertErro(() -> produtoService.getByEan("0000000000000"), HttpStatus.NOT_FOUND, "Produto não encontrado");
    }

    // ---------------------------------------------------------- atualização

    @Test
    void atualizaProdutoGeralMantendoOEan() {
        produtoService.create(produtoGeral("7890000000107", "Escova", "UN"));

        ProdutoDTO atualizado = produtoService.update("7890000000107", produtoGeral("7890000000107", "Escova Macia", "CX"));

        assertThat(atualizado.getEan()).isEqualTo("7890000000107");
        assertThat(atualizado.getNomeComercial()).isEqualTo("Escova Macia");
        assertThat(atualizado.getUnidadeDeMedida()).isEqualTo("CX");
    }

    @Test
    void atualizaCamposEspecificosDoRemedio() {
        produtoService.create(remedio(new RemedioInputDTO(), "7890000000108", "Dipirona 1g"));
        Laboratorio novoLaboratorio = laboratorioRepository.save(new Laboratorio("EMS", "EMS S/A"));

        RemedioInputDTO alteracao = remedio(new RemedioInputDTO(), "7890000000108", "Dipirona 1g");
        alteracao.setIdLaboratorio(novoLaboratorio.getId());
        alteracao.setQuantidadeDoses(20);
        RemedioOutputDTO atualizado = (RemedioOutputDTO) produtoService.update("7890000000108", alteracao);

        assertThat(atualizado.getNomeLaboratorio()).isEqualTo("EMS");
        assertThat(atualizado.getQuantidadeDoses()).isEqualTo(20f);
    }

    @Test
    void recusaAtualizacaoQueMudaOTipoDoProduto() {
        produtoService.create(produtoGeral("7890000000109", "Escova", "UN"));
        produtoService.create(remedio(new RemedioInputDTO(), "7890000000110", "Dipirona 1g"));

        // geral -> remédio
        assertErro(() -> produtoService.update("7890000000109", remedio(new RemedioInputDTO(), "7890000000109", "Escova")),
                HttpStatus.BAD_REQUEST, "tipo fundamental");
        // remédio -> geral
        assertErro(() -> produtoService.update("7890000000110", produtoGeral("7890000000110", "Dipirona", "CX")),
                HttpStatus.BAD_REQUEST, "tipo fundamental");
        // remédio -> genérico
        assertErro(() -> produtoService.update("7890000000110", remedio(new GenericoInputDTO(), "7890000000110", "Dipirona")),
                HttpStatus.BAD_REQUEST, "tipo fundamental");
        assertThat(produtoRepository.findByEan("7890000000110")).get().isInstanceOf(Remedio.class);
    }

    @Test
    void atualizacaoDeEanInexistenteRetorna404() {
        assertErro(() -> produtoService.update("0000000000000", produtoGeral("0000000000000", "X", "UN")),
                HttpStatus.NOT_FOUND, "Produto não encontrado");
    }

    // ---------------------------------------------------------- auxiliares

    private ProdutoDTO produtoGeral(String ean, String nome, String unidade) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setEan(ean);
        dto.setNomeComercial(nome);
        dto.setUnidadeDeMedida(unidade);
        dto.setTipoProduto("geral");
        return dto;
    }

    private <T extends RemedioInputDTO> T remedio(T dto, String ean, String nome) {
        dto.setEan(ean);
        dto.setNomeComercial(nome);
        dto.setUnidadeDeMedida("CX");
        dto.setTipoProduto(dto instanceof GenericoInputDTO ? "generico" : "remedio");
        dto.setIdFormula(idFormula);
        dto.setIdLaboratorio(idLaboratorio);
        dto.setQuantidadeDoses(10);
        dto.setSiglaMedidaDoses("CP");
        dto.setSiglaApresentacao("CP");
        dto.setTipoControle("isento");
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
