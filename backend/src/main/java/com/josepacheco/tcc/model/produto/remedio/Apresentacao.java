package com.josepacheco.tcc.model.produto.remedio;

import jakarta.persistence.*;

@Entity
public class Apresentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String apresentacao;

    public Apresentacao(String apresentacao) {
        this.apresentacao = apresentacao;
    }

    public Apresentacao() {
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
}
