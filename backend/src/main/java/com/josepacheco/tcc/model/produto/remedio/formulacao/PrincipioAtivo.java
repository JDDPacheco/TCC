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

    @OneToMany(mappedBy = "principioAtivo")
    private List<Composicao> composicoes = new ArrayList<>();

    public PrincipioAtivo() {}

    public PrincipioAtivo(String nome, List<Composicao> composicoes) {
        this.nome = nome;
        this.composicoes = composicoes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Composicao> getComposicoes() {
        return composicoes;
    }

    public void setComposicoes(List<Composicao> composicoes) {
        this.composicoes = composicoes;
    }
}
