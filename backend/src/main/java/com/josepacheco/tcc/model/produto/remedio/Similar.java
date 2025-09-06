package com.josepacheco.tcc.model.produto.remedio;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("similar")
public class Similar extends Remedio{
    // classe apenas de anotação, sem atributos específicos
    // vou criar uma regra para que o nome do produto seja a formula quando o remédio for similar
}
