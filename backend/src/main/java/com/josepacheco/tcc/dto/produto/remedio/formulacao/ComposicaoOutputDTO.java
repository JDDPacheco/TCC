package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.Composicao;
import io.swagger.v3.oas.annotations.media.Schema;

public class ComposicaoOutputDTO {

    @Schema(description = "código id da concentracao, é a chave.", example = "1")
    private Long id;

    @Schema(description = "nome do princípio ativo", example = "dipirona")
    private String principioAtivo;

    @Schema(description = "quantia de princípio ativo base", example = "300")
    private float quantiaPrincipio;

    @Schema(description = "unidade de medida de princípio ativo", example = "mg")
    private String unidadeMedidaPrincipio;

    @Schema(description = "quantia de excipiente (deixar em branco quando for por comprimido ou dose)", example = "5")
    private float quantiaExcipiente;

    @Schema(description = "unidade de medida do excipiente (deixar em branco quando for compirmido ou dose)", example = "ml")
    private String unidadeMedidaExcipiente;

    public ComposicaoOutputDTO() {
    }

    public ComposicaoOutputDTO(Composicao composicao){
        this.id = composicao.getId();
        this.principioAtivo = composicao.getPrincipioAtivo().getNome();
        this.quantiaPrincipio = composicao.getQuantiaPrincipio();
        this.unidadeMedidaPrincipio = composicao.getMedidaPrincipio().getSigla();
        this.quantiaExcipiente = composicao.getQuantiaExcipiente();
        if(composicao.getMedidaExcipiente() == null)
            this.unidadeMedidaExcipiente = null;
        else
            this.unidadeMedidaExcipiente = composicao.getMedidaExcipiente().getSigla();
    }

    public Long getId() {
        return id;
    }

    public float getQuantiaPrincipio() {
        return quantiaPrincipio;
    }

    public void setQuantiaPrincipio(float quantiaPrincipio) {
        this.quantiaPrincipio = quantiaPrincipio;
    }

    public String getUnidadeMedidaPrincipio() {
        return unidadeMedidaPrincipio;
    }

    public void setUnidadeMedidaPrincipio(String unidadeMedidaPrincipio) {
        this.unidadeMedidaPrincipio = unidadeMedidaPrincipio;
    }

    public float getQuantiaExcipiente() {
        return quantiaExcipiente;
    }

    public void setQuantiaExcipiente(float quantiaExcipiente) {
        this.quantiaExcipiente = quantiaExcipiente;
    }

    public String getUnidadeMedidaExcipiente() {
        return unidadeMedidaExcipiente;
    }

    public void setUnidadeMedidaExcipiente(String unidadeMedidaExcipiente) {
        this.unidadeMedidaExcipiente = unidadeMedidaExcipiente;
    }

    public String getPrincipioAtivo() {
        return principioAtivo;
    }

    public void setPrincipioAtivo(String principioAtivo) {
        this.principioAtivo = principioAtivo;
    }

}
