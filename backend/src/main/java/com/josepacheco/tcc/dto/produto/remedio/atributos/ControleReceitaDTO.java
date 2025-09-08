package com.josepacheco.tcc.dto.produto.remedio.atributos;

import com.josepacheco.tcc.model.produto.remedio.atributos.ControleReceita;
import io.swagger.v3.oas.annotations.media.Schema;

public class ControleReceitaDTO {

    @Schema(description = "tipo de controle", example = "isento")
    private String tipo; // isento, especial, sob_prescricao

    public ControleReceitaDTO(ControleReceita controle) {
        this.tipo = controle.getTipoControle();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
