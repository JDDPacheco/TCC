package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.Composicao;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Formula;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class FormulaOutputDTO {

    @Schema(description = "Código id da fórmula", example = "1")
    private Long id;

    @Schema(description = "Lista das composições da fórmula", example = "[dipirona 30 mg,...]")
    private List<String> composicoes = new ArrayList<>();

    public FormulaOutputDTO(Formula formula){
       this.id = formula.getId();
        List<Composicao> composicoes = formula.getComposicoes();
        for (Composicao composicao: composicoes){
            this.composicoes.add(composicao.toString());
        }
    }

    public FormulaOutputDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getComposicoes() {
        return composicoes;
    }

    public void setComposicoes(List<String> composicoes) {
        this.composicoes = composicoes;
    }
}
