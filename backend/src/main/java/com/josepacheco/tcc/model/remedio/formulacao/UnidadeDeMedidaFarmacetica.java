package com.josepacheco.tcc.model.remedio.formulacao;

import com.josepacheco.tcc.model.Produto;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class UnidadeDeMedidaFarmacetica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String descricao;

    @Column(nullable = false, unique = true)
    private String sigla;

    public UnidadeDeMedidaFarmacetica() {
    }

    public UnidadeDeMedidaFarmacetica(String descricao, String sigla) {
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
}
