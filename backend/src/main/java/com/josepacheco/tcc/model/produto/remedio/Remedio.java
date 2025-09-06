package com.josepacheco.tcc.model.produto.remedio;

import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Formula;
import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import com.josepacheco.tcc.model.produto.remedio.receita.ControleReceita;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("remedio")
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

    @ManyToOne
    @JoinColumn(name = "apresentacao", nullable = false)
    private Apresentacao apresentacao;

    @ManyToOne
    @JoinColumn(name = "controle", nullable = false)
    private ControleReceita controle;


    public Remedio() {}

    public Remedio(Formula formula, Laboratorio laboratorio, float quantidadeDoses, UnidadeDeMedidaFarmacetica medidaDoses, float conteudo, MedidaBasica medidaConteudo, float pesoLiquido, MedidaBasica medidaPeso, Apresentacao apresentacao, ControleReceita controle) {
        this.formula = formula;
        this.laboratorio = laboratorio;
        this.quantidadeDoses = quantidadeDoses;
        this.medidaDoses = medidaDoses;
        this.conteudo = conteudo;
        this.medidaConteudo = medidaConteudo;
        this.pesoLiquido = pesoLiquido;
        this.medidaPeso = medidaPeso;
        this.apresentacao = apresentacao;
        this.controle = controle;
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

    public Apresentacao getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(Apresentacao apresentacao) {
        this.apresentacao = apresentacao;
    }

    public ControleReceita getControle() {
        return controle;
    }

    public void setControle(ControleReceita controle) {
        this.controle = controle;
    }
}


