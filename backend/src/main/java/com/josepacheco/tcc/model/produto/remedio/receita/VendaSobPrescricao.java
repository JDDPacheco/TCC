package com.josepacheco.tcc.model.produto.remedio.receita;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("sob_prescricao")
public class VendaSobPrescricao extends ControleReceita {
}
