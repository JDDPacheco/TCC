package com.josepacheco.tcc.model.estoque;

import com.josepacheco.tcc.model.produto.Produto;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_lote_produto_codigo", columnNames = {"produto_id", "codigo_lote"}))
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private String codigoLote; // Código de identificação do lote; único por produto (laboratórios diferentes podem repetir códigos)

    @Column
    private LocalDate dataValidade; // Data de validade do lote; nula apenas no lote único de produtos gerais

    @Column(nullable = false)
    private Integer quantidadeAtual = 0; // Saldo físico do lote, sempre alterado junto com uma MovimentacaoEstoque

    public Lote() {}

    public Lote(Produto produto, String codigoLote, LocalDate dataValidade) {
        this.produto = produto;
        this.codigoLote = codigoLote;
        this.dataValidade = dataValidade;
    }

    public boolean isVencido(LocalDate hoje) {
        return dataValidade != null && dataValidade.isBefore(hoje);
    }

    public Long getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
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

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Integer quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }
}
