package com.josepacheco.tcc.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class UnidadeDeMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String descricao;

    @Column(nullable = false, unique = true)
    private String sigla;

    @OneToMany(mappedBy = "unidadeDeMedida", fetch = FetchType.LAZY)
    private List<Produto> produtos;

    public UnidadeDeMedida() {
    }

    public UnidadeDeMedida(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }
}
