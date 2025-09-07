package com.josepacheco.tcc.dto.produto.remedio;

import com.josepacheco.tcc.model.produto.remedio.Laboratorio;
import io.swagger.v3.oas.annotations.media.Schema;

public class LaboratorioOutputDTO {

    @Schema(description = "código id do Laboratório", example = "1")
    private Long id;

    @Schema(description = "marca, ou seja, nome comercial do Laboratório", example = "eurofarma")
    private String marca;

    @Schema(description = "Nome oficial completo do Laboratório", example = "EUROFARMA LABORATÓRIOS S.A.")
    private String nomeFantasia;

    public LaboratorioOutputDTO() {
    }

    public LaboratorioOutputDTO(Laboratorio laboratorio) {
        this.id = laboratorio.getId();
        this.marca = laboratorio.getMarca();
        this.nomeFantasia = laboratorio.getNomeFantasia();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
}
