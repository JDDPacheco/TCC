package com.josepacheco.tcc.dto.venda;

import com.josepacheco.tcc.model.venda.FormaPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public class VendaInputDTO {

    @NotEmpty(message = "A venda deve ter pelo menos um item.")
    @Valid
    @Schema(description = "itens vendidos (cada produto aparece uma única vez)")
    private List<ItemVendaInputDTO> itens;

    @NotNull(message = "A forma de pagamento é obrigatória.")
    @Schema(description = "DINHEIRO, CARTAO_DEBITO, CARTAO_CREDITO ou PIX", example = "DINHEIRO")
    private FormaPagamento formaPagamento;

    @PositiveOrZero(message = "O desconto não pode ser negativo.")
    @Schema(description = "desconto em R$ sobre o total da venda (opcional)", example = "1.50")
    private BigDecimal desconto;

    @PositiveOrZero(message = "O valor recebido não pode ser negativo.")
    @Schema(description = "valor entregue pelo cliente; obrigatório apenas para pagamento em dinheiro", example = "50.00")
    private BigDecimal valorRecebido;

    public VendaInputDTO() {
    }

    public List<ItemVendaInputDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemVendaInputDTO> itens) {
        this.itens = itens;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(BigDecimal valorRecebido) {
        this.valorRecebido = valorRecebido;
    }
}
