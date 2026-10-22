package com.josepacheco.tcc.dto.venda;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ItemVendaInputDTO {

    @NotBlank(message = "O EAN de cada item é obrigatório.")
    @Schema(description = "EAN do produto vendido", example = "7896004703398")
    private String ean;

    @NotNull(message = "A quantidade de cada item é obrigatória.")
    @Positive(message = "A quantidade de cada item deve ser maior que zero.")
    @Schema(description = "quantidade vendida, na unidade padrão do produto", example = "2")
    private Integer quantidade;

    @Schema(description = "confirmação de que a receita foi apresentada (obrigatória para remédios sob prescrição ou de controle especial)", example = "true")
    private Boolean receitaApresentada;

    @Valid
    @Schema(description = "dados da receita retida (obrigatórios apenas para remédios de controle especial)")
    private DadosReceitaDTO receita;

    public ItemVendaInputDTO() {
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Boolean getReceitaApresentada() {
        return receitaApresentada;
    }

    public void setReceitaApresentada(Boolean receitaApresentada) {
        this.receitaApresentada = receitaApresentada;
    }

    public DadosReceitaDTO getReceita() {
        return receita;
    }

    public void setReceita(DadosReceitaDTO receita) {
        this.receita = receita;
    }
}
