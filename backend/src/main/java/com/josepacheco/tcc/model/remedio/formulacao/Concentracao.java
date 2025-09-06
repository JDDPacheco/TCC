package com.josepacheco.tcc.model.remedio.formulacao;

import jakarta.persistence.*;

@Entity
public class Concentracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float quantiaPrincipio; // quantos grama, micrograma, miligrama do principio ativo tem em cada unidade de referencia

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medidaPrincipio", nullable = false)
    private MedidaBasica medidaPrincipio; // unidade de medida do principio em grama, micrograma, miligrama

    @Column
    private float quantiaExcipiente; // a quantidade referente da concentração, em branco quer dizer que é por dose (geralmente capsula/comprimido)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medidaExcipiente")
    private MedidaBasica medidaExcipiente; // unidade de medida do excipiente, normalmente em mililitros; em branco quer dizer que é por dose (geralmente capsula/comprimido)

    public Concentracao() {
    }

    public Concentracao(float quantiaPrincipio, MedidaBasica medidaPrincipio, float quantiaExcipiente, MedidaBasica medidaExcipiente) {
        this.quantiaPrincipio = quantiaPrincipio;
        this.medidaPrincipio = medidaPrincipio;
        this.quantiaExcipiente = quantiaExcipiente;
        this.medidaExcipiente = medidaExcipiente;
    }

    public Float getQuantiaPrincipio() {
        return quantiaPrincipio;
    }

    public MedidaBasica getMedidaPrincipio() {
        return medidaPrincipio;
    }

    public void setMedidaPrincipio(MedidaBasica medidaPrincipio) {
        this.medidaPrincipio = medidaPrincipio;
    }

    public void setQuantiaPrincipio(float quantiaPrincipio) {
        this.quantiaPrincipio = quantiaPrincipio;
    }

    public float getQuantiaExcipiente() {
        return quantiaExcipiente;
    }

    public void setQuantiaExcipiente(float quantiaExcipiente) {
        this.quantiaExcipiente = quantiaExcipiente;
    }

    public MedidaBasica getMedidaExcipiente() {
        return medidaExcipiente;
    }

    public void setMedidaExcipiente(MedidaBasica medidaExcipiente) {
        this.medidaExcipiente = medidaExcipiente;
    }
}
