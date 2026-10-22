package com.josepacheco.tcc.dto.venda;

import com.josepacheco.tcc.model.venda.ItemVenda;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ItemVendaOutputDTO {

    @Schema(description = "id do item", example = "1")
    private Long id;

    @Schema(description = "EAN do produto", example = "7896004703398")
    private String ean;

    @Schema(description = "nome comercial do produto", example = "Dipirona 1g")
    private String nomeProduto;

    @Schema(description = "código do lote de onde o item saiu", example = "A2611")
    private String codigoLote;

    @Schema(description = "validade do lote", example = "2026-11-11")
    private LocalDate dataValidade;

    @Schema(description = "quantidade vendida deste lote", example = "2")
    private Integer quantidade;

    @Schema(description = "preço unitário cobrado na venda", example = "12.90")
    private BigDecimal precoUnitario;

    @Schema(description = "preço unitário x quantidade", example = "25.80")
    private BigDecimal subtotal;

    @Schema(description = "tipo de controle do remédio ('sob_prescricao' ou 'especial'); nulo se não exige receita", example = "especial")
    private String tipoControle;

    @Schema(description = "receita apresentada (apenas itens que exigem receita)", example = "true")
    private Boolean receitaApresentada;

    @Schema(description = "dados da receita retida (apenas controle especial)")
    private DadosReceitaDTO receita;

    public ItemVendaOutputDTO() {
    }

    public ItemVendaOutputDTO(ItemVenda item) {
        this.id = item.getId();
        this.ean = item.getProduto().getEan();
        this.nomeProduto = item.getProduto().getNomeComercial();
        this.codigoLote = item.getLote().getCodigoLote();
        this.dataValidade = item.getLote().getDataValidade();
        this.quantidade = item.getQuantidade();
        this.precoUnitario = item.getPrecoUnitarioCobrado();
        this.subtotal = item.getSubtotal();
        this.tipoControle = item.getTipoControle();
        this.receitaApresentada = item.getReceitaApresentada();
        this.receita = item.getReceita() == null ? null : new DadosReceitaDTO(item.getReceita());
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

    public String getCodigoLote() {
        return codigoLote;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public String getTipoControle() {
        return tipoControle;
    }

    public Boolean getReceitaApresentada() {
        return receitaApresentada;
    }

    public DadosReceitaDTO getReceita() {
        return receita;
    }
}
