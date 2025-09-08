package com.josepacheco.tcc.model.produto.remedio.atributos;

import jakarta.persistence.*;

@Entity
public class ControleReceita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tipoControle;

    public ControleReceita() {
    }

    public ControleReceita(String tipoControle) {
        this.tipoControle = tipoControle;
    }

    public Long getId() {
        return id;
    }

    public String getTipoControle(){
        return tipoControle;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTipoControle(String tipoControle) {
        this.tipoControle = tipoControle;
    }
}
