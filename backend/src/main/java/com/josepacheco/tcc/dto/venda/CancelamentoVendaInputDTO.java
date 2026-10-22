package com.josepacheco.tcc.dto.venda;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CancelamentoVendaInputDTO {

    @NotBlank(message = "O motivo do cancelamento é obrigatório.")
    @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres.")
    @Schema(description = "motivo do cancelamento", example = "Cliente desistiu da compra")
    private String motivo;

    public CancelamentoVendaInputDTO() {
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
