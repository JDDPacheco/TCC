package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.PrincipioAtivo;
import io.swagger.v3.oas.annotations.media.Schema;

public class PrincipioAtivoOutputDTO {

    @Schema(description = "código id do princípio ativo", example = "1")
    private Long id;

    @Schema(description = "nome do princípio ativo", example = "dipirona monoidratada")
    private String nome;

    public PrincipioAtivoOutputDTO() {
    }

    public PrincipioAtivoOutputDTO(PrincipioAtivo principioAtivo) {
        this.id = principioAtivo.getId();
        this.nome = principioAtivo.getNome();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
