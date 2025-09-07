package com.josepacheco.tcc.dto.produto.remedio;

import com.josepacheco.tcc.model.produto.remedio.Apresentacao;
import io.swagger.v3.oas.annotations.media.Schema;

public class ApresentacaoOutputDTO {

    @Schema(description = "código id da apresentacao", example = "1")
    private Long id;

    @Schema(description = "descricao da apresentacao", example = "comprimido revestido")
    private String apresentacao;

    public ApresentacaoOutputDTO() {
    }

    public ApresentacaoOutputDTO(Apresentacao apresentacao) {
        this.id = apresentacao.getId();
        this.apresentacao = apresentacao.getApresentacao();
    }

    public String getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(String apresentacao) {
        this.apresentacao = apresentacao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
