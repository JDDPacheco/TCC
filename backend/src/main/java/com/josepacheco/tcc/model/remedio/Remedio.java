package com.josepacheco.tcc.model.remedio;

import com.josepacheco.tcc.model.Produto;
import com.josepacheco.tcc.model.remedio.formulacao.Formula;
import com.josepacheco.tcc.model.remedio.formulacao.MedidaBasica;
import com.josepacheco.tcc.model.remedio.formulacao.UnidadeDeMedidaFarmacetica;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("remedio")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_remedio", discriminatorType = DiscriminatorType.STRING)
public class Remedio extends Produto {

    @ManyToOne
    @JoinColumn(name = "formula", nullable = false)
    private Formula formula; // Formula do remédio

    @ManyToOne
    @JoinColumn(name = "laboratorio", nullable = false)
    private Laboratorio laboratorio; // Laboratorio que fabrica o remédio

    @Column
    private float quantidadeDoses; // Quantidade de comprimidos, cápsulas, pastilhas, doses e etc...

    @ManyToOne
    @JoinColumn(name = "medidaDoses")
    private UnidadeDeMedidaFarmacetica medidaDoses; // Comprimidos, cápsulas moles, pastilhas, doses e etc...

    @Column
    private float conteudo; // medida do volume de conteudo da embalegem; normalmente usado apenas em remédio líquidos/pastosos

    @ManyToOne
    @JoinColumn(name = "medidaConteudo")
    private MedidaBasica medidaConteudo; // unidade de medida do conteudo (em ml)
    /**
     * IMPORTANTE:  preciso criar uma regra para que o remédio tenha pelo menos uma das informações "quantidadeDoses" ou "conteudo"
     *              podendo ter as duas, mas não podendo as duas serem vazias.
     */

    @Column
    private float pesoLiquido; // peso do conteudo, geralmente para remédios em pó ou soluções

    @ManyToOne
    @JoinColumn(name = "medidaPeso")
    private MedidaBasica medidaPeso; // sempre em gramas (criar regra para adicionar automaticamente quando houver peso líquido)

    public Remedio() {}

    public Remedio(Formula formula, Laboratorio laboratorio, float quantidadeDoses, UnidadeDeMedidaFarmacetica medidaDoses, float conteudo, MedidaBasica medidaConteudo, float pesoLiquido, MedidaBasica medidaPeso) {
        this.formula = formula;
        this.laboratorio = laboratorio;
        this.quantidadeDoses = quantidadeDoses;
        this.medidaDoses = medidaDoses;
        this.conteudo = conteudo;
        this.medidaConteudo = medidaConteudo;
        this.pesoLiquido = pesoLiquido;
        this.medidaPeso = medidaPeso;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public Laboratorio getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(Laboratorio laboratorio) {
        this.laboratorio = laboratorio;
    }

    public float getQuantidadeDoses() {
        return quantidadeDoses;
    }

    public void setQuantidadeDoses(float quantidadeDoses) {
        this.quantidadeDoses = quantidadeDoses;
    }

    public UnidadeDeMedidaFarmacetica getMedidaDoses() {
        return medidaDoses;
    }

    public void setMedidaDoses(UnidadeDeMedidaFarmacetica medidaDoses) {
        this.medidaDoses = medidaDoses;
    }

    public float getConteudo() {
        return conteudo;
    }

    public void setConteudo(float conteudo) {
        this.conteudo = conteudo;
    }

    public MedidaBasica getMedidaConteudo() {
        return medidaConteudo;
    }

    public void setMedidaConteudo(MedidaBasica medidaConteudo) {
        this.medidaConteudo = medidaConteudo;
    }

    public float getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(float pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public MedidaBasica getMedidaPeso() {
        return medidaPeso;
    }

    public void setMedidaPeso(MedidaBasica medidaPeso) {
        this.medidaPeso = medidaPeso;
    }
}


