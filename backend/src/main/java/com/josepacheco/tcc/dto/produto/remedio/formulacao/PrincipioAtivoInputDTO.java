package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.PrincipioAtivo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class PrincipioAtivoInputDTO {

    @NotBlank(message = "O nome do princípio ativo é obrigatório.")
    @Schema(description = "nome do princípio ativo", example = "dipirona monoidratada")
    private String nome;

    public PrincipioAtivoInputDTO() {
    }

    public PrincipioAtivoInputDTO(PrincipioAtivo principioAtivo) {
        this.nome = principioAtivo.getNome();
    }

    public PrincipioAtivo build(){
        PrincipioAtivo principioAtivo = new PrincipioAtivo();
        principioAtivo.setNome(this.nome);
        return principioAtivo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
