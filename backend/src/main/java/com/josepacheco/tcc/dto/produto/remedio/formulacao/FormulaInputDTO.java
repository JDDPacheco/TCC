package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.Composicao;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Formula;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.ComposicaoRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class FormulaInputDTO {

    @NotEmpty(message = "A lista de ids das composições é obrigatória.")
    @Schema(description = "lista de ids das composições", example = "[1,3,5]")
    private List<Long> idComposicoes;

   public Formula build(ComposicaoRepository composicaoRepository){
        // Resolvendo dependências
        List<Composicao> composicoes = new ArrayList<>();
        for(Long idComposicao: this.idComposicoes){
            composicoes.add(composicaoRepository.getReferenceById(idComposicao));
        }

        // Criando objeto de Fórmula
        Formula formula = new Formula();
        formula.setComposicoes(composicoes);

        return formula;
    }

    public FormulaInputDTO() {
    }

    public List<Long> getIdComposicoes() {
        return idComposicoes;
    }

    public void setIdComposicoes(List<Long> idComposicoes) {
        this.idComposicoes = idComposicoes;
    }
}
