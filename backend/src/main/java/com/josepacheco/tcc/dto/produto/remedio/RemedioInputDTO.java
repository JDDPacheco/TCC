package com.josepacheco.tcc.dto.produto.remedio;

import com.josepacheco.tcc.dto.produto.ProdutoDTO;
import com.josepacheco.tcc.model.produto.MedidaPadrao;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.model.produto.remedio.atributos.Apresentacao;
import com.josepacheco.tcc.model.produto.remedio.atributos.ControleReceita;
import com.josepacheco.tcc.model.produto.remedio.atributos.Laboratorio;
import com.josepacheco.tcc.model.produto.remedio.atributos.MedidaFarmaceutica;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Formula;
import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ApresentacaoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ControleReceitaRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.LaboratorioRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.MedidaFarmaceuticaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.FormulaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.MedidaBasicaRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RemedioInputDTO extends ProdutoDTO {

    @NotNull(message = "O id da fórmula é obrigatório.")
    @Schema(description = "id de uma fórmula já criada previamente", example = "1")
    private Long idFormula;

    @NotNull(message = "O id do laboratório é obrigatório.")
    @Schema(description = "id de um laboratório já registrado previamente", example = "1")
    private Long idLaboratorio;

    @Schema(description = "quantidade de doses (comprimidos, cápsulas, pastilhas, doses e etc...) do remédio, apenas número (quando houver)", example = "120")
    private float quantidadeDoses;

    @Schema(description = "sigla da unidade de medidada de doses - conforme encontrado em: /api/produto/remedio/medida_farmaceutica", example = "CP (para comprimidos)")
    private String siglaMedidaDoses;

    @Schema(description = "conteúdo medida do volume de conteúdo da embalagem; normalmente usado apenas em remédio líquidos/pastosos (apenas números)", example = "20")
    private float conteudo;

    @Schema(description = "sigla da unidade de medida de doses - conforme encontrado em: /api/produto/remedio/formulacao/medida_basica", example = "G - grama, ML - mililitro, etc...")
    private String siglaMedidaConteudo;

    @Schema(description = "peso do conteudo, geralmente para remédios em pó ou soluções (apenas números)", example = "120")
    private float pesoLiquido;

    @NotBlank
    @Schema(description = "sigla da apresentação - conforme encontrado em: /api/produto/remedio/apresentacao", example = "CP - Comprimido, CR - Comprimido Revestido, etc...")
    private String siglaApresentacao;

    @NotBlank
    @Schema(description = "tipo de controlo - conforme encontrado em: /api/produto/remedio/receita", example = "sob_prescricao")
    private String tipoControle;

    public RemedioInputDTO(){
        super();
    }

    public Remedio build(FormulaRepository formulaRepository, LaboratorioRepository laboratorioRepository,
                         MedidaFarmaceuticaRepository medidaFarmaceuticaRepository, MedidaBasicaRepository medidaBasicaRepository,
                         ApresentacaoRepository apresentacaoRepository, MedidaPadraoRepository medidaPadraoRepository,
                         ControleReceitaRepository controleReceitaRepository){

        // Resolvendo dependências obrigatórias
        MedidaPadrao medidaPadraoProduto = medidaPadraoRepository.findBySigla(this.getUnidadeDeMedida()); // superclasse
        Formula formula = formulaRepository.getReferenceById(this.idFormula);
        Laboratorio laboratorio = laboratorioRepository.getReferenceById(this.idLaboratorio);
        Apresentacao apresentacao = apresentacaoRepository.findBySigla(this.siglaApresentacao);
        ControleReceita controleReceita = controleReceitaRepository.findByTipoControle(this.tipoControle);
        MedidaFarmaceutica medidaFarmaceutica = medidaFarmaceuticaRepository.findBySigla(this.siglaMedidaDoses);

        // Criando objeto de Remedio
        Remedio remedio = new Remedio();

        // Inserindo dados obrigatórios
        remedio.setEan(this.getEan());
        remedio.setNomeComercial(this.getNomeComercial());
        remedio.setUnidadeDeMedida(medidaPadraoProduto);
        remedio.setFormula(formula);
        remedio.setLaboratorio(laboratorio);
        remedio.setApresentacao(apresentacao);
        remedio.setControle(controleReceita);
        remedio.setMedidaDoses(medidaFarmaceutica);

        // Inserindo dados opcionais
        // Verificando se os dados do peso líquido foram fornecidos antes de atribuir.
        if (this.pesoLiquido != 0) {
            remedio.setPesoLiquido(this.pesoLiquido);
            MedidaBasica medida = medidaBasicaRepository.findBySigla("g");
            remedio.setMedidaPeso(medida);
        }

        return remedio;
    }

//    public Remedio build(FormulaRepository formulaRepository, LaboratorioRepository laboratorioRepository,
//                         MedidaBasicaRepository medidaBasicaRepository, ApresentacaoRepository apresentacaoRepository,
//                         MedidaPadraoRepository medidaPadraoRepository, ControleReceitaRepository controleReceitaRepository){
//
//        // Resolvendo dependências obrigatórias
//        MedidaPadrao medidaPadraoProduto = medidaPadraoRepository.findBySigla(this.getUnidadeDeMedida()); // superclasse
//        Formula formula = formulaRepository.getReferenceById(this.idFormula);
//        Laboratorio laboratorio = laboratorioRepository.getReferenceById(this.idLaboratorio);
//        Apresentacao apresentacao = apresentacaoRepository.findBySigla(this.siglaApresentacao);
//        ControleReceita controleReceita = controleReceitaRepository.findByTipo(this.tipoControle);
//        MedidaBasica medidaBasica = medidaBasicaRepository.findBySigla(this.siglaMedidaConteudo);
//
//        // Criando objeto de Remedio
//        Remedio remedio = new Remedio();
//
//        // Inserindo dados obrigatórios
//        remedio.setEan(this.getEan());
//        remedio.setNomeComercial(this.getNomeComercial());
//        remedio.setUnidadeDeMedida(medidaPadraoProduto);
//        remedio.setFormula(formula);
//        remedio.setLaboratorio(laboratorio);
//        remedio.setApresentacao(apresentacao);
//        remedio.setControle(controleReceita);
//        remedio.setMedidaConteudo(medidaBasica);
//
//        // Inserindo dados opcionais
//        // Verificando se os dados do peso líquido foram fornecidos antes de atribuir.
//        if (this.pesoLiquido != 0) {
//            remedio.setPesoLiquido(this.pesoLiquido);
//            MedidaBasica medida = medidaBasicaRepository.findBySigla("g");
//            remedio.setMedidaPeso(medida);
//        }
//
//        return remedio;
//    }

    public Long getIdFormula() {
        return idFormula;
    }

    public void setIdFormula(Long idFormula) {
        this.idFormula = idFormula;
    }

    public Long getIdLaboratorio() {
        return idLaboratorio;
    }

    public void setIdLaboratorio(Long idLaboratorio) {
        this.idLaboratorio = idLaboratorio;
    }

    public float getQuantidadeDoses() {
        return quantidadeDoses;
    }

    public void setQuantidadeDoses(float quantidadeDoses) {
        this.quantidadeDoses = quantidadeDoses;
    }

    public String getSiglaMedidaDoses() {
        return siglaMedidaDoses;
    }

    public void setSiglaMedidaDoses(String siglaMedidaDoses) {
        this.siglaMedidaDoses = siglaMedidaDoses;
    }

    public float getConteudo() {
        return conteudo;
    }

    public void setConteudo(float conteudo) {
        this.conteudo = conteudo;
    }

    public String getSiglaMedidaConteudo() {
        return siglaMedidaConteudo;
    }

    public void setSiglaMedidaConteudo(String siglaMedidaConteudo) {
        this.siglaMedidaConteudo = siglaMedidaConteudo;
    }

    public float getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(float pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public String getSiglaApresentacao() {
        return siglaApresentacao;
    }

    public void setSiglaApresentacao(String siglaApresentacao) {
        this.siglaApresentacao = siglaApresentacao;
    }

    public String getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(String tipoControle) {
        this.tipoControle = tipoControle;
    }
}
