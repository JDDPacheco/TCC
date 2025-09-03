package com.josepacheco.tcc.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_produto", discriminatorType = DiscriminatorType.STRING)
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeComercial;

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
