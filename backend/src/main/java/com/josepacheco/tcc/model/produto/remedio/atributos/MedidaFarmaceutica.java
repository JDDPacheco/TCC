package com.josepacheco.tcc.model.produto.remedio.atributos;

import jakarta.persistence.*;

@Entity
public class MedidaFarmaceutica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String descricao;

    @Column(nullable = false, unique = true)
    private String sigla;

    public MedidaFarmaceutica() {
    }

    public MedidaFarmaceutica(String sigla, String descricao) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public Long getId() {
        return id;
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
}
