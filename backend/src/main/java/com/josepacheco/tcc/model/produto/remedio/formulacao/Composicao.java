package com.josepacheco.tcc.model.produto.remedio.formulacao;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Composicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "principioAtivo", nullable = false)
    private PrincipioAtivo principioAtivo;

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

    public Composicao() {
    }

    public PrincipioAtivo getPrincipioAtivo() {
        return principioAtivo;
    }

    public void setPrincipioAtivo(PrincipioAtivo principioAtivo) {
        this.principioAtivo = principioAtivo;
    }

    public float getQuantiaPrincipio() {
        return quantiaPrincipio;
    }

    public void setQuantiaPrincipio(float quantiaPrincipio) {
        this.quantiaPrincipio = quantiaPrincipio;
    }

    public MedidaBasica getMedidaPrincipio() {
        return medidaPrincipio;
    }

    public void setMedidaPrincipio(MedidaBasica medidaPrincipio) {
        this.medidaPrincipio = medidaPrincipio;
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

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        String quantiaPrincipioStr = (quantiaPrincipio % 1 == 0) ?
                String.valueOf((int) quantiaPrincipio) :
                String.valueOf(quantiaPrincipio);

        String quantiaExcipienteStr = (quantiaExcipiente % 1 == 0) ?
                String.valueOf((int) quantiaExcipiente) :
                String.valueOf(quantiaExcipiente);

        if (quantiaExcipiente == 0.0) {
            return principioAtivo.getNome() + " " +
                    quantiaPrincipioStr + " " + medidaPrincipio.getSigla();
        } else if(quantiaExcipiente == 1.0){
            return principioAtivo.getNome() + " " +
                    quantiaPrincipioStr + " " + medidaPrincipio.getSigla() +
                    " / " + medidaExcipiente.getSigla();
        } else {
            return principioAtivo.getNome() + " " +
                    quantiaPrincipioStr + " " + medidaPrincipio.getSigla() +
                    " / " + quantiaExcipienteStr + " " + medidaExcipiente.getSigla();
        }
    }

}
