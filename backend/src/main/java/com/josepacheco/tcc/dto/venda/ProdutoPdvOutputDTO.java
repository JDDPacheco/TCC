package com.josepacheco.tcc.dto.venda;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Informações de que o caixa (PDV) precisa para colocar um produto no carrinho.
 */
public class ProdutoPdvOutputDTO {

    @Schema(description = "EAN do produto", example = "7896004703398")
    private String ean;

    @Schema(description = "nome comercial", example = "Dipirona 1g")
    private String nomeComercial;

    @Schema(description = "'geral', 'remedio' ou 'generico'", example = "remedio")
    private String tipoProduto;

    @Schema(description = "unidade padrão de venda", example = "CX")
    private String unidadeDeMedida;

    @Schema(description = "preço de venda atual; nulo se o produto não tem preço cadastrado", example = "12.90")
    private BigDecimal precoAtual;

    @Schema(description = "saldo disponível para venda (lotes dentro da validade)", example = "41")
    private Integer saldoDisponivel;

    @Schema(description = "'isento', 'sob_prescricao' ou 'especial'; nulo para produtos gerais", example = "isento")
    private String tipoControle;

    @Schema(description = "verdadeiro quando a venda exige confirmação de receita", example = "false")
    private boolean exigeReceita;

    @Schema(description = "verdadeiro quando, além da confirmação, os dados da receita devem ser registrados (controle especial)", example = "false")
    private boolean exigeDadosReceita;

    public ProdutoPdvOutputDTO() {
    }

    public ProdutoPdvOutputDTO(String ean, String nomeComercial, String tipoProduto, String unidadeDeMedida, BigDecimal precoAtual,
                               Integer saldoDisponivel, String tipoControle, boolean exigeReceita, boolean exigeDadosReceita) {
        this.ean = ean;
        this.nomeComercial = nomeComercial;
        this.tipoProduto = tipoProduto;
        this.unidadeDeMedida = unidadeDeMedida;
        this.precoAtual = precoAtual;
        this.saldoDisponivel = saldoDisponivel;
        this.tipoControle = tipoControle;
        this.exigeReceita = exigeReceita;
        this.exigeDadosReceita = exigeDadosReceita;
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

    public String getUnidadeDeMedida() {
        return unidadeDeMedida;
    }

    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }

    public Integer getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public String getTipoControle() {
        return tipoControle;
    }

    public boolean isExigeReceita() {
        return exigeReceita;
    }

    public boolean isExigeDadosReceita() {
        return exigeDadosReceita;
    }
}
