package com.josepacheco.tcc.dto.estoque;

import com.josepacheco.tcc.model.estoque.Lote;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoteOutputDTO {

    @Schema(description = "id do lote", example = "1")
    private Long id;

    @Schema(description = "EAN do produto", example = "7891058017392")
    private String ean;

    @Schema(description = "nome comercial do produto", example = "Dipirona 1g")
    private String nomeProduto;

    @Schema(description = "código do lote ('UNICO' para produtos gerais)", example = "L2309A")
    private String codigoLote;

    @Schema(description = "data de validade; nula no lote único de produtos gerais", example = "2027-03-31")
    private LocalDate dataValidade;

    @Schema(description = "saldo atual do lote", example = "20")
    private Integer quantidadeAtual;

    @Schema(description = "VENCIDO, A_VENCER (dentro do prazo de alerta), OK ou SEM_VALIDADE", example = "OK")
    private String situacao;

    @Schema(description = "dias até o vencimento (negativo se já venceu); nulo se não há validade", example = "160")
    private Long diasParaVencer;

    public LoteOutputDTO() {
    }

    public LoteOutputDTO(Lote lote, LocalDate hoje, int diasAlertaVencimento) {
        this.id = lote.getId();
        this.ean = lote.getProduto().getEan();
        this.nomeProduto = lote.getProduto().getNomeComercial();
        this.codigoLote = lote.getCodigoLote();
        this.dataValidade = lote.getDataValidade();
        this.quantidadeAtual = lote.getQuantidadeAtual();
        if (dataValidade == null) {
            this.situacao = "SEM_VALIDADE";
        } else {
            this.diasParaVencer = ChronoUnit.DAYS.between(hoje, dataValidade);
            if (lote.isVencido(hoje))
                this.situacao = "VENCIDO";
            else if (diasParaVencer <= diasAlertaVencimento)
                this.situacao = "A_VENCER";
            else
                this.situacao = "OK";
        }
    }

    public Long getId() {
        return id;
    }

    public String getEan() {
        return ean;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public String getSituacao() {
        return situacao;
    }

    public Long getDiasParaVencer() {
        return diasParaVencer;
    }
}
