package com.josepacheco.tcc.dto.estoque;

import com.josepacheco.tcc.model.estoque.Lote;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.remedio.Generico;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.List;

/**
 * Posição de estoque de um produto: soma dos lotes, separando o que pode ser vendido do que está vencido.
 */
public class SaldoProdutoOutputDTO {

    @Schema(description = "EAN do produto", example = "7891058017392")
    private String ean;

    @Schema(description = "nome comercial do produto", example = "Dipirona 1g")
    private String nomeComercial;

    @Schema(description = "'geral', 'remedio' ou 'generico'", example = "remedio")
    private String tipoProduto;

    @Schema(description = "sigla da unidade padrão em que o estoque é contado", example = "CX")
    private String unidadeDeMedida;

    @Schema(description = "estoque mínimo configurado (nulo se não configurado)", example = "5")
    private Integer estoqueMinimo;

    @Schema(description = "soma dos lotes dentro da validade (disponível para venda)", example = "38")
    private Integer saldoDisponivel;

    @Schema(description = "soma dos lotes vencidos que ainda têm saldo (aguardando baixa por perda)", example = "2")
    private Integer saldoVencido;

    @Schema(description = "verdadeiro quando há estoque mínimo e o saldo disponível está igual ou abaixo dele", example = "false")
    private boolean abaixoDoMinimo;

    @Schema(description = "lotes do produto, do que vence primeiro para o último")
    private List<LoteOutputDTO> lotes;

    public SaldoProdutoOutputDTO() {
    }

    public SaldoProdutoOutputDTO(Produto produto, List<Lote> lotes, LocalDate hoje, int diasAlertaVencimento) {
        this.ean = produto.getEan();
        this.nomeComercial = produto.getNomeComercial();
        this.tipoProduto = tipoDoProduto(produto);
        this.unidadeDeMedida = produto.getUnidadeDeMedida().getSigla();
        this.estoqueMinimo = produto.getEstoqueMinimo();
        this.saldoDisponivel = lotes.stream().filter(l -> !l.isVencido(hoje)).mapToInt(Lote::getQuantidadeAtual).sum();
        this.saldoVencido = lotes.stream().filter(l -> l.isVencido(hoje)).mapToInt(Lote::getQuantidadeAtual).sum();
        this.abaixoDoMinimo = estoqueMinimo != null && saldoDisponivel <= estoqueMinimo;
        this.lotes = lotes.stream().map(l -> new LoteOutputDTO(l, hoje, diasAlertaVencimento)).toList();
    }

    // O produto pode chegar como proxy do Hibernate (via Lote), então o tipo real é verificado após "desembrulhar" o objeto
    private static String tipoDoProduto(Produto produto) {
        Object real = Hibernate.unproxy(produto);
        if (real instanceof Generico) return "generico";
        if (real instanceof Remedio) return "remedio";
        return "geral";
    }

    public String getEan() {
        return ean;
    }

    public String getNomeComercial() {
        return nomeComercial;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public String getUnidadeDeMedida() {
        return unidadeDeMedida;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public Integer getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public Integer getSaldoVencido() {
        return saldoVencido;
    }

    public boolean isAbaixoDoMinimo() {
        return abaixoDoMinimo;
    }

    public List<LoteOutputDTO> getLotes() {
        return lotes;
    }
}
