package com.josepacheco.tcc.dto.preco;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PrecoInputDTO {

    @NotBlank(message = "O EAN do produto é obrigatório.")
    private String ean;

    @NotNull(message = "O valor de venda é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor de venda deve ser maior que zero.")
    private BigDecimal valorVenda;

    public PrecoInputDTO() {
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }
}