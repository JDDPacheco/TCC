package com.josepacheco.tcc.dto.preco;

import com.josepacheco.tcc.model.preco.Preco;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PrecoOutputDTO {

    private Long id;
    private String ean;
    private String nomeProduto;
    private BigDecimal valorVenda;
    private LocalDateTime dataInicioVigencia;

    public PrecoOutputDTO() {
    }

    public PrecoOutputDTO(Preco preco) {
        this.id = preco.getId();
        this.ean = preco.getProduto().getEan();
        this.nomeProduto = preco.getProduto().getNomeComercial();
        this.valorVenda = preco.getValorVenda();
        this.dataInicioVigencia = preco.getDataInicioVigencia();
    }

    public Long getId() {
        return id;
    }

    public String getEan() {
        return ean;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public LocalDateTime getDataInicioVigencia() {
        return dataInicioVigencia;
    }
}