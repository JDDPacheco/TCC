package com.josepacheco.tcc.model.remedio;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("generico")
public class Generico extends Remedio{
    // classe apenas de anotação, sem atributos específicos
    // vou criar uma regra para que o nome do produto seja a formula quando o remédio for generico
}
