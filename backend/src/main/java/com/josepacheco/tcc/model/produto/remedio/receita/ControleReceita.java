package com.josepacheco.tcc.model.produto.remedio.receita;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_controle", discriminatorType = DiscriminatorType.STRING)
public abstract class ControleReceita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
