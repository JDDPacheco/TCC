package com.josepacheco.tcc.model.remedio;

import com.josepacheco.tcc.model.Produto;
import com.josepacheco.tcc.model.remedio.formulacao.Formula;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("remedio")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_remedio", discriminatorType = DiscriminatorType.STRING)
public class Remedio extends Produto {
    private Formula formula; // OnetoOne
    private 
}
