package com.josepacheco.tcc.model.preco;

import com.josepacheco.tcc.model.produto.Produto;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Preco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com o Produto
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal precoCusto; // O valor que a drogaria paga

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorVenda; // O valor que o cliente paga

    @Column(nullable = false)
    private LocalDateTime dataInicioVigencia; // Data a partir da qual este preço é válido

//    @Column(nullable = false)
//    private boolean isAtivo = true; // Indica se este é o preço atualmente ativo

    public Preco() {
    }

    public Preco(Produto produto, BigDecimal valorVenda, LocalDateTime dataInicioVigencia) {
        this.produto = produto;
        this.valorVenda = valorVenda;
        this.dataInicioVigencia = dataInicioVigencia;
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

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public LocalDateTime getDataInicioVigencia() {
        return dataInicioVigencia;
    }

    public void setDataInicioVigencia(LocalDateTime dataInicioVigencia) {
        this.dataInicioVigencia = dataInicioVigencia;
    }
}