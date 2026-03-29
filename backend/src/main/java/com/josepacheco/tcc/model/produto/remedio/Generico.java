package com.josepacheco.tcc.model.produto.remedio;

import com.josepacheco.tcc.model.produto.MedidaPadrao;
import com.josepacheco.tcc.model.produto.remedio.atributos.Apresentacao;
import com.josepacheco.tcc.model.produto.remedio.atributos.ControleReceita;
import com.josepacheco.tcc.model.produto.remedio.atributos.Laboratorio;
import com.josepacheco.tcc.model.produto.remedio.atributos.MedidaFarmaceutica;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Formula;
import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("generico")
public class Generico extends Remedio{
    public Generico(String nomeComercial, String ean, MedidaPadrao unidadeDeMedida, Formula formula, Laboratorio laboratorio, float quantidadeDoses, MedidaFarmaceutica medidaDoses, float conteudo, MedidaBasica medidaConteudo, float pesoLiquido, MedidaBasica medidaPeso, Apresentacao apresentacao, ControleReceita controle) {
        super(nomeComercial, ean, unidadeDeMedida, formula, laboratorio, quantidadeDoses, medidaDoses, conteudo, medidaConteudo, pesoLiquido, medidaPeso, apresentacao, controle);
        setNomeComercial(this.getFormula().toString());
    }
    // classe apenas de anotação, sem atributos específicos
    // vou criar uma regra para que o nome do produto seja a formula quando o remédio for generico
}
