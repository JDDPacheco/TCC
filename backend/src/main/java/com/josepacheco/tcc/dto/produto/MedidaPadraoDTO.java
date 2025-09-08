package com.josepacheco.tcc.dto.produto;

import com.josepacheco.tcc.model.produto.MedidaPadrao;
import io.swagger.v3.oas.annotations.media.Schema;

public class MedidaPadraoDTO {

    @Schema(description = "Sigla da Unidade de Medida Padrão de produtos.", example = "CX")
    private String sigla;

    @Schema(description = "Descrição da Unidade de Medida Padrão de produtos.", example = "Caixa")
    private String descricao;

    public MedidaPadraoDTO() {
    }

    public MedidaPadraoDTO(MedidaPadrao medidaPadrao) {
        this.sigla = medidaPadrao.getSigla();
        this.descricao = medidaPadrao.getDescricao();
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
