package com.josepacheco.tcc.dto.produto.remedio;

import com.josepacheco.tcc.model.produto.remedio.Generico;

public class GenericoOutputDTO extends RemedioOutputDTO {
    // Apenas anotação
    public GenericoOutputDTO(Generico generico) {
        super(generico); // Chama o construtor da superclasse RemedioOutputDTO
    }
}
