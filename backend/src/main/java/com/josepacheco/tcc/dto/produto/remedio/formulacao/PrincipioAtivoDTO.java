package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.PrincipioAtivo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

public class PrincipioAtivoDTO {

    @Null(message = "O id deve ser omitido na criação e atualização.")
    @Schema(description = "código id do princípio ativo", example = "1")
    private Long id;

    @NotBlank(message = "O nome do princípio ativo é obrigatório.")
    @Schema(description = "nome do princípio ativo", example = "dipirona monoidratada")
    private String nome;

    public PrincipioAtivoDTO() {
    }

    public PrincipioAtivoDTO(PrincipioAtivo principioAtivo) {
        this.id = principioAtivo.getId();
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
