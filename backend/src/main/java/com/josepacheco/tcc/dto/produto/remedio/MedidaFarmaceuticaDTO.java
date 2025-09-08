package com.josepacheco.tcc.dto.produto.remedio;

import com.josepacheco.tcc.model.produto.remedio.atributos.MedidaFarmaceutica;
import io.swagger.v3.oas.annotations.media.Schema;

public class MedidaFarmaceuticaDTO {

    @Schema(description = "Descrição da unidade de medida farmacêutica", example = "dose")
    private String descricao;

    @Schema(description = "Sigla da unidade de medida farmacêutica", example = "DS")
    private String sigla;

    public MedidaFarmaceuticaDTO() {
    }

    public MedidaFarmaceuticaDTO(MedidaFarmaceutica unidadeDeMedidaFarmacetica) {
        this.descricao = unidadeDeMedidaFarmacetica.getDescricao();
        this.sigla = unidadeDeMedidaFarmacetica.getSigla();
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
