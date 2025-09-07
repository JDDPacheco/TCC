package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class MedidaBasicaInputDTO {

    @NotBlank(message = "O nome da unidade de medida é obrigatório.")
    @Schema(description = "nome da unidade de medida", example = "miligrama")
    private String nome;

    @NotBlank(message = "A sigla da unidade de medida é obrigatória.")
    @Schema(description = "sigla da unidade de medida", example = "mg")
    private String sigla;

    public MedidaBasicaInputDTO() {}

    public MedidaBasica build(){
        MedidaBasica medidaBasica = new MedidaBasica();
        medidaBasica.setNome(this.nome);
        medidaBasica.setSigla(this.sigla);
        return medidaBasica;
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
