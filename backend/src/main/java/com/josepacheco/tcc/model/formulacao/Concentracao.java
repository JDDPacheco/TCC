package com.josepacheco.tcc.model.formulacao;

import jakarta.persistence.*;

@Entity
public class Concentracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Float concentracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medidaBasica", nullable = false)
    private MedidaBasica medidaBasica;

    public Concentracao() {
    }

    public Concentracao(Float concentracao, MedidaBasica medidaBasica) {
        this.concentracao = concentracao;
        this.medidaBasica = medidaBasica;
    }

    public Float getConcentracao() {
        return concentracao;
    }

    public void setConcentracao(Float concentracao) {
        this.concentracao = concentracao;
    }

    public MedidaBasica getMedidaBasica() {
        return medidaBasica;
    }

    public void setMedidaBasica(MedidaBasica medidaBasica) {
        this.medidaBasica = medidaBasica;
    }
}
