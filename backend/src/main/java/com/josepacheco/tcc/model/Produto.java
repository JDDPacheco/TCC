package com.josepacheco.tcc.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ean;

    @ManyToOne
    @JoinColumn(name = "unidadeDeMedida", nullable = false)
    private UnidadeDeMedida unidadeDeMedida;

    public Produto() {
    }

    public Produto(String ean, UnidadeDeMedida unidadeDeMedida) {
        this.ean = ean;
        this.unidadeDeMedida = unidadeDeMedida;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public UnidadeDeMedida getUnidadeDeMedida() {
       return unidadeDeMedida;
    }

    public void setUnidadeDeMedida(UnidadeDeMedida unidadeDeMedida) {
        this.unidadeDeMedida = unidadeDeMedida;
    }
}
