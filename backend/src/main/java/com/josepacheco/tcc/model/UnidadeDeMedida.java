package com.josepacheco.tcc.model;

import jakarta.persistence.*;

// Essa Classe tem as unidades de medida genéricas para qualquer produto: Caixa (CX), Vidro (VD), Envelope (EV), Bisnaga (BG), Garrafa (GF), Unidade (UN)
@Entity
public class UnidadeDeMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String descricao;

    @Column(nullable = false, unique = true)
    private String sigla;

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
}
