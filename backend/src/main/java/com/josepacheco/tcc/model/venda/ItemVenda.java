package com.josepacheco.tcc.model.venda;

import com.josepacheco.tcc.model.estoque.Lote;
import com.josepacheco.tcc.model.produto.Produto;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Item vendido de um lote específico. Quando a baixa FEFO divide a quantidade entre lotes,
 * a venda tem um ItemVenda por lote do mesmo produto (com os mesmos dados de receita).
 */
@Entity
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Produto produto;

    // Rastreia o lote específico que foi vendido (chave para controle de medicamentos)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Lote lote;

    @Column(nullable = false)
    private Integer quantidade;

    // Armazena o preço que foi cobrado no momento da venda (imutável)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitarioCobrado;

    // Tipo de controle do remédio no momento da venda ("sob_prescricao", "especial"); nulo para itens sem receita
    @Column(length = 30)
    private String tipoControle;

    // Operador confirmou que a receita foi apresentada (itens sob prescrição ou de controle especial)
    @Column
    private Boolean receitaApresentada;

    // Dados da receita retida; apenas para remédios de controle especial
    @Embedded
    private DadosReceita receita;

    public ItemVenda() {}

    public BigDecimal getSubtotal() {
        return precoUnitarioCobrado.multiply(BigDecimal.valueOf(quantidade));
    }

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

    public String getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(String tipoControle) {
        this.tipoControle = tipoControle;
    }

    public Boolean getReceitaApresentada() {
        return receitaApresentada;
    }

    public void setReceitaApresentada(Boolean receitaApresentada) {
        this.receitaApresentada = receitaApresentada;
    }

    public DadosReceita getReceita() {
        return receita;
    }

    public void setReceita(DadosReceita receita) {
        this.receita = receita;
    }
}
