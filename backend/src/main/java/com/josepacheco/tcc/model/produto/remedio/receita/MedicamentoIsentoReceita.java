package com.josepacheco.tcc.model.produto.remedio.receita;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("isento")
public class MedicamentoIsentoReceita extends ControleReceita {
}
