package com.josepacheco.tcc.model.produto;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_produto", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("geral")
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
    private MedidaPadrao unidadeDeMedida;

    @Column
    private Integer estoqueMinimo; // quantidade mínima desejada em estoque (opcional); abaixo dela o produto entra no alerta de estoque baixo

    public Produto() {
    }

    public Long getId() {
        return id;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Produto(String nomeComercial, String ean, MedidaPadrao unidadeDeMedida) {
        this.nomeComercial = nomeComercial;
        this.ean = ean;
        this.unidadeDeMedida = unidadeDeMedida;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getNomeComercial() {
        return nomeComercial;
    }

    public void setNomeComercial(String nomeComercial) {
        this.nomeComercial = nomeComercial;
    }

    public MedidaPadrao getUnidadeDeMedida() {
       return unidadeDeMedida;
    }

    public void setUnidadeDeMedida(MedidaPadrao unidadeDeMedida) {
        this.unidadeDeMedida = unidadeDeMedida;
    }
}
