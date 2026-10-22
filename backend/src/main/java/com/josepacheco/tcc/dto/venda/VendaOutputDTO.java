package com.josepacheco.tcc.dto.venda;

import com.josepacheco.tcc.model.venda.Venda;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VendaOutputDTO {

    @Schema(description = "número (id) da venda", example = "12")
    private Long id;

    @Schema(description = "data e hora da venda", example = "2026-10-22T15:30:00")
    private LocalDateTime dataVenda;

    @Schema(description = "CONCLUIDA ou CANCELADA", example = "CONCLUIDA")
    private String status;

    @Schema(description = "itens vendidos (um por lote)")
    private List<ItemVendaOutputDTO> itens;

    @Schema(description = "soma dos subtotais dos itens", example = "38.70")
    private BigDecimal valorBruto;

    @Schema(description = "desconto aplicado", example = "1.70")
    private BigDecimal desconto;

    @Schema(description = "valor pago (bruto - desconto)", example = "37.00")
    private BigDecimal valorTotal;

    @Schema(description = "DINHEIRO, CARTAO_DEBITO, CARTAO_CREDITO ou PIX", example = "DINHEIRO")
    private String formaPagamento;

    @Schema(description = "valor entregue pelo cliente (apenas dinheiro)", example = "50.00")
    private BigDecimal valorRecebido;

    @Schema(description = "troco devolvido (apenas dinheiro)", example = "13.00")
    private BigDecimal troco;

    @Schema(description = "data e hora do cancelamento (se cancelada)")
    private LocalDateTime dataCancelamento;

    @Schema(description = "motivo do cancelamento (se cancelada)")
    private String motivoCancelamento;

    public VendaOutputDTO() {
    }

    public VendaOutputDTO(Venda venda) {
        this.id = venda.getId();
        this.dataVenda = venda.getDataVenda();
        this.status = venda.getStatus().name();
        this.itens = venda.getItens().stream().map(ItemVendaOutputDTO::new).toList();
        this.valorBruto = venda.getValorBruto();
        this.desconto = venda.getDesconto();
        this.valorTotal = venda.getValorTotal();
        this.formaPagamento = venda.getFormaPagamento().name();
        this.valorRecebido = venda.getValorRecebido();
        this.troco = venda.getTroco();
        this.dataCancelamento = venda.getDataCancelamento();
        this.motivoCancelamento = venda.getMotivoCancelamento();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public String getStatus() {
        return status;
    }

    public List<ItemVendaOutputDTO> getItens() {
        return itens;
    }

    public BigDecimal getValorBruto() {
        return valorBruto;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public BigDecimal getTroco() {
        return troco;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }
}
