package com.josepacheco.tcc.dto.estoque;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class EstoqueDTO {

    @NotBlank(message = "A quantia de princípio ativo é obrigatória.")
    @Schema(description = "quantia de princípio ativo base", example = "300")
    private String nomeProduto;

    @NotBlank(message = "A quantidade do produto é obrigatória.")
    @Schema(description = "quantidade do produto", example = "300")
    private Integer quantidade;

}
