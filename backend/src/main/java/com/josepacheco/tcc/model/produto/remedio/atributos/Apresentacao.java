package com.josepacheco.tcc.model.produto.remedio.atributos;

import jakarta.persistence.*;

@Entity
public class Apresentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sigla;

    @Column(nullable = false, unique = true)
    private String apresentacao;

    public Apresentacao() {
    }

    public Apresentacao(String sigla, String apresentacao) {
        this.sigla = sigla;
        this.apresentacao = apresentacao;
    }

    public Long getId() {
        return id;
    }

    public String getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(String apresentacao) {
        this.apresentacao = apresentacao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
