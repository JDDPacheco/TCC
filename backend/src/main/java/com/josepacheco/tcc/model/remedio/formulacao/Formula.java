package com.josepacheco.tcc.model.remedio.formulacao;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Formula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "formula_composicao",
            joinColumns = @JoinColumn(name = "formula_id"),
            inverseJoinColumns = @JoinColumn(name = "composicao_id")
    )
    private List<Composicao> composicoes = new ArrayList<>();

    public Formula() {}

    public Formula(List<Composicao> composicoes) {
        this.composicoes = composicoes;
    }

    public List<Composicao> getComposicoes() {
        return composicoes;
    }

    public void setComposicoes(List<Composicao> composicoes) {
        this.composicoes = composicoes;
    }
}
