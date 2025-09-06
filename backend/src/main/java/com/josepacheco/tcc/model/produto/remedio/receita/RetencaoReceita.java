package com.josepacheco.tcc.model.produto.remedio.receita;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("especial")
public class RetencaoReceita extends ControleReceita {
}
