package com.josepacheco.tcc.dto.estoque;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

public class EstoqueMinimoInputDTO {

    @PositiveOrZero(message = "O estoque mínimo não pode ser negativo.")
    @Schema(description = "quantidade mínima desejada em estoque; null remove o mínimo do produto", example = "5")
    private Integer estoqueMinimo;

    public EstoqueMinimoInputDTO() {
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }
}
