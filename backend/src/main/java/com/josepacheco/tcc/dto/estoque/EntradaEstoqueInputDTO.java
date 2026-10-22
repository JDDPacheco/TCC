package com.josepacheco.tcc.dto.estoque;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EntradaEstoqueInputDTO {

    @NotBlank(message = "O EAN do produto é obrigatório.")
    @Schema(description = "EAN (código de barras) do produto que está entrando", example = "7891058017392")
    private String ean;

    @Schema(description = "código do lote impresso na embalagem. Obrigatório para remédios; ignorado para produtos gerais, que usam um lote único.", example = "L2309A")
    private String codigoLote;

    @Schema(description = "data de validade do lote (aaaa-mm-dd). Obrigatória para remédios; ignorada para produtos gerais.", example = "2027-03-31")
    private LocalDate dataValidade;

    @NotNull(message = "A quantidade é obrigatória.")
    @Positive(message = "A quantidade deve ser maior que zero.")
    @Schema(description = "quantidade recebida, na unidade padrão do produto (ex.: caixas)", example = "20")
    private Integer quantidade;

    @NotNull(message = "O custo unitário é obrigatório.")
    @PositiveOrZero(message = "O custo unitário não pode ser negativo.")
    @Schema(description = "custo de compra por unidade (zero para bonificação)", example = "7.35")
    private BigDecimal custoUnitario;

    @NotBlank(message = "O fornecedor é obrigatório.")
    @Size(max = 255, message = "O nome do fornecedor deve ter no máximo 255 caracteres.")
    @Schema(description = "nome do fornecedor/distribuidora", example = "Distribuidora Santa Cruz")
    private String fornecedor;

    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
    @Schema(description = "observação opcional", example = "Pedido 1234")
    private String observacao;

    public EntradaEstoqueInputDTO() {
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
