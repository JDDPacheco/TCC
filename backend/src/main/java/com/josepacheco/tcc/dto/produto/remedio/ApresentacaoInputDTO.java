package com.josepacheco.tcc.dto.produto.remedio;

import com.josepacheco.tcc.model.produto.remedio.Apresentacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ApresentacaoInputDTO {

    @NotBlank(message = "A descrição da apresentação é obrigatório.")
    @Schema(description = "descrição da apresentação", example = "comprimido revestido")
    private String apresentacao;

    public ApresentacaoInputDTO() {
    }

    public ApresentacaoInputDTO(Apresentacao apresentacao) {
        this.apresentacao = apresentacao.getApresentacao();
    }

    public Apresentacao build(){
        Apresentacao apresentacao = new Apresentacao();
        apresentacao.setApresentacao(this.apresentacao);
        return apresentacao;
    }

    public String getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(String apresentacao) {
        this.apresentacao = apresentacao;
    }
}
