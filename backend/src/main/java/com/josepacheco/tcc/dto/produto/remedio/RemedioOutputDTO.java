package com.josepacheco.tcc.dto.produto.remedio;

import com.josepacheco.tcc.dto.produto.ProdutoDTO;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Composicao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.stream.Collectors;

public class RemedioOutputDTO extends ProdutoDTO {

    @Schema(description = "Texto com a fórmula do remédio", example = "dipirona 30 mg + cafeína 100 mg")
    private String formula;

    @Schema(description = "Nome comercial do laboratório", example = "Teuto")
    private String nomeLaboratorio;

    @Schema(description = "Quantidade de doses (comprimidos, cápsulas, pastilhas, doses e etc...) do remédio, apenas número", example = "120")
    private float quantidadeDoses;

    @Schema(description = "Descrição da unidade de medida de doses.", example = "Comprimidos")
    private String descricaoMedidaDoses;

    @Schema(description = "conteúdo medida do volume de conteúdo da embalagem; normalmente usado apenas em remédio líquidos/pastosos (apenas números)", example = "20")
    private float conteudo;

    @Schema(description = "Descricao da unidade de medida de doses.", example = "grama")
    private String descricaoMedidaConteudo;

    @Schema(description = "peso do conteudo, geralmente para remédios em pó ou soluções (apenas números)", example = "120")
    private float pesoLiquido;

    @Schema(description = "sempre grama", example = "gramas")
    private String unidadeMedidaPesoLiquido;

    @Schema(description = "Apresentação do remédio.", example = "Comprimido Revestido")
    private String apresentacao;

    @Schema(description = "Tipo de controle de receita do remédio.", example = "sob_prescricao")
    private String tipoControle;

    public RemedioOutputDTO() {}

    public RemedioOutputDTO(Remedio remedio){
        super(remedio);
        this.formula = remedio.getFormula().getComposicoes()
                .stream()
                .map(Composicao::toString) // Chama o seu metodo toString() para cada composição
                .collect(Collectors.joining(" + ")); // Junta as strings;
        this.nomeLaboratorio = remedio.getLaboratorio().getMarca();
        this.quantidadeDoses = remedio.getQuantidadeDoses();
        this.conteudo = remedio.getConteudo();
        this.pesoLiquido = remedio.getPesoLiquido();
        if (remedio.getMedidaDoses() != null) {
            this.descricaoMedidaDoses = remedio.getMedidaDoses().getDescricao();
        }
        if (remedio.getMedidaConteudo() != null) {
            this.descricaoMedidaConteudo = remedio.getMedidaConteudo().getNome();
        }
        if (remedio.getMedidaPeso() != null) {
            this.unidadeMedidaPesoLiquido = remedio.getMedidaPeso().getNome();
        } else if (this.pesoLiquido > 0) { // Se tem peso, mas não tem unidade definida, assume "g"
            this.unidadeMedidaPesoLiquido = "grama";
        }
        this.apresentacao = remedio.getApresentacao().getApresentacao();
        this.tipoControle = remedio.getControle().getTipoControle();
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getNomeLaboratorio() {
        return nomeLaboratorio;
    }

    public void setNomeLaboratorio(String nomeLaboratorio) {
        this.nomeLaboratorio = nomeLaboratorio;
    }

    public float getQuantidadeDoses() {
        return quantidadeDoses;
    }

    public void setQuantidadeDoses(float quantidadeDoses) {
        this.quantidadeDoses = quantidadeDoses;
    }

    public String getDescricaoMedidaDoses() {
        return descricaoMedidaDoses;
    }

    public void setDescricaoMedidaDoses(String descricaoMedidaDoses) {
        this.descricaoMedidaDoses = descricaoMedidaDoses;
    }

    public float getConteudo() {
        return conteudo;
    }

    public void setConteudo(float conteudo) {
        this.conteudo = conteudo;
    }

    public String getDescricaoMedidaConteudo() {
        return descricaoMedidaConteudo;
    }

    public void setDescricaoMedidaConteudo(String descricaoMedidaConteudo) {
        this.descricaoMedidaConteudo = descricaoMedidaConteudo;
    }

    public float getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(float pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public String getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(String apresentacao) {
        this.apresentacao = apresentacao;
    }

    public String getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(String tipoControle) {
        this.tipoControle = tipoControle;
    }

    public String getUnidadeMedidaPesoLiquido() {
        return unidadeMedidaPesoLiquido;
    }

    public void setUnidadeMedidaPesoLiquido(String unidadeMedidaPesoLiquido) {
        this.unidadeMedidaPesoLiquido = unidadeMedidaPesoLiquido;
    }
}
