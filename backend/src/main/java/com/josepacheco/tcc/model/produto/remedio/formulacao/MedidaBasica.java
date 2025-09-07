package com.josepacheco.tcc.model.produto.remedio.formulacao;

import jakarta.persistence.*;

// Esta Classe é para unidades de medidas do S.I. (grama, litro e suas derivações)
@Entity
public class MedidaBasica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, unique = true)
    private String sigla;

    public MedidaBasica() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

}
