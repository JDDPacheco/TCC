package com.josepacheco.tcc.dto.estoque;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Entrada para o ajuste de inventário: informa a quantidade contada fisicamente,
 * e o sistema registra a diferença em relação ao saldo atual do lote.
 */
public class AjusteEstoqueInputDTO {

    @NotNull(message = "O lote é obrigatório.")
    @Schema(description = "id do lote contado", example = "1")
    private Long idLote;

    @NotNull(message = "A quantidade contada é obrigatória.")
    @PositiveOrZero(message = "A quantidade contada não pode ser negativa.")
    @Schema(description = "quantidade encontrada na contagem física", example = "18")
    private Integer quantidadeContada;

    @NotBlank(message = "A observação (motivo) é obrigatória.")
    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
    @Schema(description = "motivo do ajuste", example = "Inventário mensal")
    private String observacao;

    public AjusteEstoqueInputDTO() {
    }

    public Long getIdLote() {
        return idLote;
    }

    public void setIdLote(Long idLote) {
        this.idLote = idLote;
    }

    public Integer getQuantidadeContada() {
        return quantidadeContada;
    }

    public void setQuantidadeContada(Integer quantidadeContada) {
        this.quantidadeContada = quantidadeContada;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
