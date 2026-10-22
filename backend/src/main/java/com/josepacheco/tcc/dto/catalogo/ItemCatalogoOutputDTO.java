package com.josepacheco.tcc.dto.catalogo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Linha do catálogo da tela inicial: dados do produto, saldo disponível e preço atual.
 */
public class ItemCatalogoOutputDTO {

    @Schema(description = "EAN do produto", example = "7896004703398")
    private String ean;

    @Schema(description = "nome comercial", example = "Dipirona Monoidratada 1g")
    private String nomeComercial;

    @Schema(description = "'geral', 'remedio' ou 'generico'", example = "remedio")
    private String tipoProduto;

    @Schema(description = "marca do laboratório (nula para produtos gerais)", example = "Prati-Donaduzzi")
    private String marca;

    @Schema(description = "fórmula: composições separadas por ' + ' (nula para produtos gerais)", example = "dipirona monoidratada 1 g")
    private String formula;

    @Schema(description = "'isento', 'sob_prescricao' ou 'especial' (nulo para produtos gerais)", example = "isento")
    private String tipoControle;

    @Schema(description = "unidade padrão em que o estoque é contado", example = "CX")
    private String unidadeDeMedida;

    @Schema(description = "saldo disponível para venda (lotes dentro da validade)", example = "41")
    private Integer saldoDisponivel;

    @Schema(description = "preço de venda atual (nulo se não há preço cadastrado)", example = "12.90")
    private BigDecimal precoAtual;

    public ItemCatalogoOutputDTO() {
    }

    public ItemCatalogoOutputDTO(String ean, String nomeComercial, String tipoProduto, String marca, String formula,
                                 String tipoControle, String unidadeDeMedida, Integer saldoDisponivel, BigDecimal precoAtual) {
        this.ean = ean;
        this.nomeComercial = nomeComercial;
        this.tipoProduto = tipoProduto;
        this.marca = marca;
        this.formula = formula;
        this.tipoControle = tipoControle;
        this.unidadeDeMedida = unidadeDeMedida;
        this.saldoDisponivel = saldoDisponivel;
        this.precoAtual = precoAtual;
    }

    public String getEan() {
        return ean;
    }

    public String getNomeComercial() {
        return nomeComercial;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public String getMarca() {
        return marca;
    }

    public String getFormula() {
        return formula;
    }

    public String getTipoControle() {
        return tipoControle;
    }

    public String getUnidadeDeMedida() {
        return unidadeDeMedida;
    }

    public Integer getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }
}
