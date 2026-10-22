package com.josepacheco.tcc.dto.estoque;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Entrada para perdas e devoluções: uma quantidade que sai (perda) ou volta (devolução) para um lote.
 */
public class MovimentacaoEstoqueInputDTO {

    @NotNull(message = "O lote é obrigatório.")
    @Schema(description = "id do lote afetado", example = "1")
    private Long idLote;

    @NotNull(message = "A quantidade é obrigatória.")
    @Positive(message = "A quantidade deve ser maior que zero.")
    @Schema(description = "quantidade perdida ou devolvida", example = "2")
    private Integer quantidade;

    @NotBlank(message = "A observação (motivo) é obrigatória.")
    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
    @Schema(description = "motivo da movimentação", example = "Caixas amassadas no transporte")
    private String observacao;

    public MovimentacaoEstoqueInputDTO() {
    }

    public Long getIdLote() {
        return idLote;
    }

    public void setIdLote(Long idLote) {
        this.idLote = idLote;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
