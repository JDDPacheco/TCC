package com.josepacheco.tcc.dto.venda;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Fechamento de caixa de um dia: considera apenas vendas concluídas (as canceladas são só contadas).
 */
public class ResumoVendasOutputDTO {

    @Schema(description = "dia resumido", example = "2026-10-22")
    private LocalDate data;

    @Schema(description = "número de vendas concluídas", example = "15")
    private long quantidadeVendas;

    @Schema(description = "número de vendas canceladas no dia", example = "1")
    private long quantidadeCanceladas;

    @Schema(description = "soma dos valores brutos", example = "520.40")
    private BigDecimal valorBruto;

    @Schema(description = "soma dos descontos", example = "12.00")
    private BigDecimal descontos;

    @Schema(description = "total recebido (bruto - descontos)", example = "508.40")
    private BigDecimal valorTotal;

    @Schema(description = "valor médio por venda concluída", example = "33.89")
    private BigDecimal ticketMedio;

    @Schema(description = "totais por forma de pagamento (todas as formas, mesmo sem vendas)")
    private List<TotalPorFormaPagamento> porFormaPagamento;

    public ResumoVendasOutputDTO() {
    }

    public ResumoVendasOutputDTO(LocalDate data, long quantidadeVendas, long quantidadeCanceladas, BigDecimal valorBruto,
                                 BigDecimal descontos, BigDecimal valorTotal, BigDecimal ticketMedio,
                                 List<TotalPorFormaPagamento> porFormaPagamento) {
        this.data = data;
        this.quantidadeVendas = quantidadeVendas;
        this.quantidadeCanceladas = quantidadeCanceladas;
        this.valorBruto = valorBruto;
        this.descontos = descontos;
        this.valorTotal = valorTotal;
        this.ticketMedio = ticketMedio;
        this.porFormaPagamento = porFormaPagamento;
    }

    public record TotalPorFormaPagamento(String formaPagamento, long quantidadeVendas, BigDecimal valorTotal) {
    }

    public LocalDate getData() {
        return data;
    }

    public long getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public long getQuantidadeCanceladas() {
        return quantidadeCanceladas;
    }

    public BigDecimal getValorBruto() {
        return valorBruto;
    }

    public BigDecimal getDescontos() {
        return descontos;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public List<TotalPorFormaPagamento> getPorFormaPagamento() {
        return porFormaPagamento;
    }
}
