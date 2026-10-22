package com.josepacheco.tcc.dto.estoque;

import com.josepacheco.tcc.model.estoque.MovimentacaoEstoque;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovimentacaoEstoqueOutputDTO {

    @Schema(description = "id da movimentação", example = "1")
    private Long id;

    @Schema(description = "id do lote movimentado", example = "1")
    private Long idLote;

    @Schema(description = "código do lote", example = "L2309A")
    private String codigoLote;

    @Schema(description = "validade do lote", example = "2027-03-31")
    private LocalDate dataValidade;

    @Schema(description = "EAN do produto", example = "7891058017392")
    private String ean;

    @Schema(description = "nome comercial do produto", example = "Dipirona 1g")
    private String nomeProduto;

    @Schema(description = "ENTRADA, SAIDA_VENDA, AJUSTE_INVENTARIO, PERDA ou DEVOLUCAO", example = "ENTRADA")
    private String tipo;

    @Schema(description = "variação do saldo (positiva = entrou, negativa = saiu)", example = "20")
    private Integer quantidade;

    @Schema(description = "saldo do lote após a movimentação", example = "20")
    private Integer saldoApos;

    @Schema(description = "data e hora da movimentação", example = "2026-10-22T14:00:00")
    private LocalDateTime dataHora;

    @Schema(description = "observação/motivo", example = "Inventário mensal")
    private String observacao;

    @Schema(description = "custo unitário de compra (apenas em entradas)", example = "7.35")
    private BigDecimal custoUnitario;

    @Schema(description = "fornecedor (apenas em entradas)", example = "Distribuidora Santa Cruz")
    private String fornecedor;

    public MovimentacaoEstoqueOutputDTO() {
    }

    public MovimentacaoEstoqueOutputDTO(MovimentacaoEstoque movimentacao) {
        this.id = movimentacao.getId();
        this.idLote = movimentacao.getLote().getId();
        this.codigoLote = movimentacao.getLote().getCodigoLote();
        this.dataValidade = movimentacao.getLote().getDataValidade();
        this.ean = movimentacao.getLote().getProduto().getEan();
        this.nomeProduto = movimentacao.getLote().getProduto().getNomeComercial();
        this.tipo = movimentacao.getTipo().name();
        this.quantidade = movimentacao.getQuantidade();
        this.saldoApos = movimentacao.getSaldoApos();
        this.dataHora = movimentacao.getDataHora();
        this.observacao = movimentacao.getObservacao();
        this.custoUnitario = movimentacao.getCustoUnitario();
        this.fornecedor = movimentacao.getFornecedor();
    }

    public Long getId() {
        return id;
    }

    public Long getIdLote() {
        return idLote;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public String getEan() {
        return ean;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Integer getSaldoApos() {
        return saldoApos;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getObservacao() {
        return observacao;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public String getFornecedor() {
        return fornecedor;
    }
}
