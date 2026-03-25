package com.josepacheco.tcc.model.produto.remedio.formulacao;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class PrincipioAtivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    public PrincipioAtivo() {}

    public PrincipioAtivo(String nome) {
        this.nome = nome;
    }

    public Long getId(){
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
