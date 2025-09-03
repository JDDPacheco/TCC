package com.josepacheco.tcc.model.formulacao;

import jakarta.persistence.*;

@Entity
public class PrincipioAtivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    private String principioAtivo;

    public PrincipioAtivo() {}

    public String getPrincipioAtivo() {
        return principioAtivo;
    }

    public void setPrincipioAtivo(String principioAtivo) {
        this.principioAtivo = principioAtivo;
    }
}
