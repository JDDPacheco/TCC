package com.josepacheco.tcc.service.produto;

import com.josepacheco.tcc.dto.produto.remedio.LaboratorioDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.ComposicaoDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.FormulaInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.FormulaOutputDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.PrincipioAtivoDTO;
import com.josepacheco.tcc.service.produto.remedio.atributos.LaboratorioService;
import com.josepacheco.tcc.service.produto.remedio.formulacao.ComposicaoService;
import com.josepacheco.tcc.service.produto.remedio.formulacao.FormulaService;
import com.josepacheco.tcc.service.produto.remedio.formulacao.PrincipioAtivoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cadeia de formulação dos remédios (princípio ativo → composição → fórmula) e cadastro de laboratórios.
 */
@SpringBootTest
@Transactional
class FormulacaoServiceTest {

    @Autowired
    private PrincipioAtivoService principioAtivoService;
    @Autowired
    private ComposicaoService composicaoService;
    @Autowired
    private FormulaService formulaService;
    @Autowired
    private LaboratorioService laboratorioService;

    @BeforeEach
    void cadastrarPrincipioAtivo() {
        principioAtivoService.create(principioAtivo("ibuprofeno"));
    }

    // ---------------------------------------------------------- princípio ativo

    @Test
    void cadastraConsultaECorrigePrincipioAtivo() {
        PrincipioAtivoDTO criado = principioAtivoService.create(principioAtivo("paracetamol"));

        assertThat(criado.getId()).isNotNull();
        assertThat(principioAtivoService.getById(criado.getId()).getNome()).isEqualTo("paracetamol");
        assertThat(principioAtivoService.list()).extracting(PrincipioAtivoDTO::getNome).contains("paracetamol", "ibuprofeno");

        assertThat(principioAtivoService.update(criado.getId(), "paracetamol (corrigido)").getNome()).isEqualTo("paracetamol (corrigido)");
    }

    // ---------------------------------------------------------- composição

    @Test
    void cadastraComposicaoResolvendoPrincipioEUnidadePorNomeESigla() {
        ComposicaoDTO criada = composicaoService.create(composicao("ibuprofeno", 400, "mg", 0, null));

        assertThat(criada.getId()).isNotNull();
        assertThat(criada.getPrincipioAtivo()).isEqualTo("ibuprofeno");
        assertThat(criada.getQuantiaPrincipio()).isEqualTo(400f);
        assertThat(criada.getUnidadeMedidaPrincipio()).isEqualTo("mg");
        assertThat(criada.getUnidadeMedidaExcipiente()).isNull();
    }

    @Test
    void atualizaComposicao() {
        ComposicaoDTO criada = composicaoService.create(composicao("ibuprofeno", 400, "mg", 0, null));

        ComposicaoDTO atualizada = composicaoService.update(criada.getId(), composicao("ibuprofeno", 600, "mg", 0, null));

        assertThat(atualizada.getQuantiaPrincipio()).isEqualTo(600f);
        assertThat(composicaoService.getById(criada.getId()).getQuantiaPrincipio()).isEqualTo(600f);
    }

    // ---------------------------------------------------------- fórmula

    @Test
    void formulaDescreveCadaComposicaoComPrincipioQuantidadeEUnidade() {
        Long semExcipiente = composicaoService.create(composicao("ibuprofeno", 400, "mg", 0, null)).getId();
        Long porUnidadeDeExcipiente = composicaoService.create(composicao("ibuprofeno", 50, "mg", 1, "ml")).getId();
        Long comQuantidadeDeExcipiente = composicaoService.create(composicao("ibuprofeno", 100, "mg", 5, "ml")).getId();
        Long quantiaFracionada = composicaoService.create(composicao("ibuprofeno", 0.5f, "g", 0, null)).getId();

        FormulaOutputDTO formula = formulaService.create(formula(semExcipiente, porUnidadeDeExcipiente, comQuantidadeDeExcipiente, quantiaFracionada));

        assertThat(formula.getId()).isNotNull();
        assertThat(formula.getComposicoes()).containsExactly(
                "ibuprofeno 400 mg",
                "ibuprofeno 50 mg / ml",
                "ibuprofeno 100 mg / 5 ml",
                "ibuprofeno 0.5 g");
    }

    @Test
    void atualizaAsComposicoesDaFormula() {
        Long composicaoA = composicaoService.create(composicao("ibuprofeno", 200, "mg", 0, null)).getId();
        Long composicaoB = composicaoService.create(composicao("ibuprofeno", 400, "mg", 0, null)).getId();
        Long idFormula = formulaService.create(formula(composicaoA)).getId();

        FormulaOutputDTO atualizada = formulaService.update(idFormula, formula(composicaoB));

        assertThat(atualizada.getComposicoes()).containsExactly("ibuprofeno 400 mg");
        assertThat(formulaService.list()).extracting(FormulaOutputDTO::getId).contains(idFormula);
    }

    // ---------------------------------------------------------- laboratório

    @Test
    void cadastraConsultaECorrigeLaboratorio() {
        LaboratorioDTO criado = laboratorioService.create(laboratorio("Medley", "Medley Farmacêutica Ltda"));

        assertThat(laboratorioService.getById(criado.getId()).getNomeFantasia()).isEqualTo("Medley Farmacêutica Ltda");

        LaboratorioDTO corrigido = laboratorioService.update(criado.getId(), laboratorio("Medley", "Medley Indústria Farmacêutica Ltda"));
        assertThat(corrigido.getMarca()).isEqualTo("Medley");
        assertThat(corrigido.getNomeFantasia()).isEqualTo("Medley Indústria Farmacêutica Ltda");
        assertThat(laboratorioService.list()).extracting(LaboratorioDTO::getMarca).contains("Medley", "Prati-Donaduzzi");
    }

    // ---------------------------------------------------------- auxiliares

    private PrincipioAtivoDTO principioAtivo(String nome) {
        PrincipioAtivoDTO dto = new PrincipioAtivoDTO();
        dto.setNome(nome);
        return dto;
    }

    private ComposicaoDTO composicao(String principio, float quantia, String unidade, float quantiaExcipiente, String unidadeExcipiente) {
        ComposicaoDTO dto = new ComposicaoDTO();
        dto.setPrincipioAtivo(principio);
        dto.setQuantiaPrincipio(quantia);
        dto.setUnidadeMedidaPrincipio(unidade);
        dto.setQuantiaExcipiente(quantiaExcipiente);
        dto.setUnidadeMedidaExcipiente(unidadeExcipiente);
        return dto;
    }

    private FormulaInputDTO formula(Long... idsComposicoes) {
        FormulaInputDTO dto = new FormulaInputDTO();
        dto.setIdComposicoes(List.of(idsComposicoes));
        return dto;
    }

    private LaboratorioDTO laboratorio(String marca, String nomeFantasia) {
        LaboratorioDTO dto = new LaboratorioDTO();
        dto.setMarca(marca);
        dto.setNomeFantasia(nomeFantasia);
        return dto;
    }
}
