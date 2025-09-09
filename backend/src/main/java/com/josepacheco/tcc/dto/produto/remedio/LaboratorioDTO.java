package com.josepacheco.tcc.dto.produto.remedio;

import com.josepacheco.tcc.model.produto.remedio.atributos.Laboratorio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

public class LaboratorioDTO {

    @Null(message = "O id deve ser omitido na criação e atualização.")
    @Schema(description = "código id do Laboratório", example = "1")
    private Long id;

    @NotBlank(message = "A marca é obrigatório.")
    @Schema(description = "marca, ou seja, nome comercial do Laboratório", example = "eurofarma")
    private String marca;

    @NotBlank(message = "O Nome Fantasia é obrigatório.")
    @Schema(description = "Nome oficial completo do Laboratório", example = "EUROFARMA LABORATÓRIOS S.A.")
    private String nomeFantasia;

    public LaboratorioDTO() {
    }

    public LaboratorioDTO(Laboratorio laboratorio) {
        this.id = laboratorio.getId();
        this.marca = laboratorio.getMarca();
        this.nomeFantasia = laboratorio.getNomeFantasia();
    }

    public Laboratorio build(){
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setMarca(this.marca);
        laboratorio.setNomeFantasia(this.nomeFantasia);
        return laboratorio;
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
