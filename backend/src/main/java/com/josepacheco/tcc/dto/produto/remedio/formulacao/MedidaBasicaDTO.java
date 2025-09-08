package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import io.swagger.v3.oas.annotations.media.Schema;

public class MedidaBasicaDTO {

    @Schema(description = "nome da unidade de medida", example = "miligrama")
    private String nome;
    @Schema(description = "sigla da unidade de medida", example = "mg")
    private String sigla;

    public MedidaBasicaDTO() {}

    public MedidaBasicaDTO(MedidaBasica medidaBasica) {
        this.nome = medidaBasica.getNome();
        this.sigla = medidaBasica.getSigla();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

}
