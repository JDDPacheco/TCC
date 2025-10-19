package com.josepacheco.tcc.model.estoque;

import com.josepacheco.tcc.model.produto.Produto;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Produto produto;

    @Column(nullable = false, unique = true)
    private String codigoLote; // Código de identificação do lote

    @Column(nullable = false)
    private LocalDate dataValidade; // Data de validade do lote

    public Lote() {}

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
}
