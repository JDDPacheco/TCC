package com.josepacheco.tcc.model.produto.remedio.formulacao;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Composicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "principioAtivo", nullable = false)
    private PrincipioAtivo principioAtivo;

    @ManyToOne
    @JoinColumn(name = "concentracao", nullable = false)
    private Concentracao concentracao;

    @ManyToMany(mappedBy = "composicoes", fetch = FetchType.LAZY)
    private List<Formula> formulas = new ArrayList<>();

    public Composicao() {
    }

    public Composicao(PrincipioAtivo principioAtivo, Concentracao concentracao, List<Formula> formulas) {
        this.principioAtivo = principioAtivo;
        this.concentracao = concentracao;
        this.formulas = formulas;
    }

    public PrincipioAtivo getPrincipioAtivo() {
        return principioAtivo;
    }

    public void setPrincipioAtivo(PrincipioAtivo principioAtivo) {
        this.principioAtivo = principioAtivo;
    }

    public Concentracao getConcentracao() {
        return concentracao;
    }

    public void setConcentracao(Concentracao concentracao) {
        this.concentracao = concentracao;
    }

    public List<Formula> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<Formula> formulas) {
        this.formulas = formulas;
    }
}
