package com.josepacheco.tcc.model.venda;

import com.josepacheco.tcc.model.estoque.Lote;
import com.josepacheco.tcc.model.produto.Produto;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Rastreia o lote específico que foi vendido (chave para controle de medicamentos)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Column(nullable = false)
    private Integer quantidade;

    // Armazena o preço que foi cobrado no momento da venda (imutável)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitarioCobrado;

    public ItemVenda() {}

    public Long getId() {
        return id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitarioCobrado() {
        return precoUnitarioCobrado;
    }

    public void setPrecoUnitarioCobrado(BigDecimal precoUnitarioCobrado) {
        this.precoUnitarioCobrado = precoUnitarioCobrado;
    }
}