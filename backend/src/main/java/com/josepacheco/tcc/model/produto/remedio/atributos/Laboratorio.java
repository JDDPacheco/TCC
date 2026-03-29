package com.josepacheco.tcc.model.produto.remedio.atributos;

import jakarta.persistence.*;

@Entity
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String marca; // nome usado comercialmente

    @Column(nullable = false, unique = true)
    private String nomeFantasia; // nome completo da empresa

    public Laboratorio() {
    }

    public Laboratorio(String marca, String nomeFantasia) {
        this.marca = marca;
        this.nomeFantasia = nomeFantasia;
    }

    public Long getId() {
        return id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
}
