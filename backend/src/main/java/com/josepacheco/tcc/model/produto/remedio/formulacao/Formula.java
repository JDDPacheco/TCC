package com.josepacheco.tcc.model.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.Remedio;
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

    @OneToMany(mappedBy = "formula", fetch = FetchType.LAZY)
    private List<Remedio> remedios = new ArrayList<>();

    public Formula() {}

    public Formula(List<Composicao> composicoes, List<Remedio> remedios) {
        this.composicoes = composicoes;
        this.remedios = remedios;
    }

    public List<Composicao> getComposicoes() {
        return composicoes;
    }

    public void setComposicoes(List<Composicao> composicoes) {
        this.composicoes = composicoes;
    }

    public List<Remedio> getRemedios() {
        return remedios;
    }

    public void setRemedios(List<Remedio> remedios) {
        this.remedios = remedios;
    }
}
