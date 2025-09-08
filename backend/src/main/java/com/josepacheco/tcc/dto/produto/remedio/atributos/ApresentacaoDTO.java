package com.josepacheco.tcc.dto.produto.remedio.atributos;

import com.josepacheco.tcc.model.produto.remedio.atributos.Apresentacao;
import io.swagger.v3.oas.annotations.media.Schema;

public class ApresentacaoDTO {

    @Schema(description = "Silga da apresentação.", example = "CR")
    private String sigla;

    @Schema(description = "Descrição da apresentação.", example = "Comprimido Revestido")
    private String apresentacao;

    public ApresentacaoDTO() {
    }

    public ApresentacaoDTO(Apresentacao apresentacao) {
        this.sigla = apresentacao.getSigla();
        this.apresentacao = apresentacao.getApresentacao();
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(String apresentacao) {
        this.apresentacao = apresentacao;
    }
}
