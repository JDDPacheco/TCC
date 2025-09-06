package com.josepacheco.tcc.model.produto.remedio;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("referencia")
public class Referencia extends Remedio{
    // classe apenas de anotação, sem atributos específicos
    // vou criar uma regra para que o nome do produto seja a formula quando o remédio for referencia
}
