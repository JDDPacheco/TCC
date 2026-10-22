package com.josepacheco.tcc.model.estoque;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registro imutável de cada alteração no saldo de um lote (histórico do estoque).
 * Nenhuma movimentação é editada ou apagada: correções são feitas com novas movimentações.
 */
@Entity
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Lote lote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private Integer quantidade; // variação do saldo: positiva para entradas, negativa para saídas

    @Column(nullable = false)
    private Integer saldoApos; // saldo do lote logo depois desta movimentação

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 500)
    private String observacao; // motivo do ajuste/perda/devolução (obrigatório nesses casos)

    @Column(precision = 10, scale = 2)
    private BigDecimal custoUnitario; // custo de compra por unidade; apenas em ENTRADA

    @Column
    private String fornecedor; // nome do fornecedor; apenas em ENTRADA

    public MovimentacaoEstoque() {}

    public MovimentacaoEstoque(Lote lote, TipoMovimentacao tipo, Integer quantidade, Integer saldoApos, LocalDateTime dataHora,
                               String observacao, BigDecimal custoUnitario, String fornecedor) {
        this.lote = lote;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.saldoApos = saldoApos;
        this.dataHora = dataHora;
        this.observacao = observacao;
        this.custoUnitario = custoUnitario;
        this.fornecedor = fornecedor;
    }

    public Long getId() {
        return id;
    }

    public Lote getLote() {
        return lote;
    }

    public TipoMovimentacao getTipo() {
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
