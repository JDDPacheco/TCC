package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.Composicao;
import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import com.josepacheco.tcc.model.produto.remedio.formulacao.PrincipioAtivo;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.MedidaBasicaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.PrincipioAtivoRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ComposicaoInputDTO {

    @NotBlank(message = "O nome do princípio ativo é obrigatório.")
    @Schema(description = "nome do princípio ativo", example = "dipirona")
    private String principioAtivo;

    @NotBlank(message = "A quantia de princípio ativo é obrigatória.")
    @Schema(description = "quantia de princípio ativo base", example = "300")
    private float quantiaPrincipio;

    @NotBlank(message = "A unidade de medida de princípio ativo é obrigatória.")
    @Schema(description = "unidade de medida de princípio ativo", example = "mg")
    private String unidadeMedidaPrincipio;

    @Schema(description = "quantia de excipiente (não enviar quando for por comprimido ou dose)", example = "5")
    private float quantiaExcipiente;

    @Schema(description = "unidade de medida do excipiente (não enviar quando for compirmido ou dose)", example = "ml")
    private String unidadeMedidaExcipiente;

    public Composicao build(MedidaBasicaRepository medidaBasicaRepository, PrincipioAtivoRepository principioAtivoRepository){
        // Resolvendo dependências
        PrincipioAtivo principioAtivo = principioAtivoRepository.findByNome(this.principioAtivo);
        MedidaBasica medidaBasicaPrincipio = medidaBasicaRepository.findBySigla(this.unidadeMedidaPrincipio);
//        MedidaBasica medidaBasicaExcipiente = medidaBasicaRepository.findBySigla(this.unidadeMedidaExcipiente);

        // Criando objeto de Concentração
        Composicao composicao = new Composicao();
        composicao.setPrincipioAtivo(principioAtivo);           // obrigatório
        composicao.setQuantiaPrincipio(this.quantiaPrincipio);  // obrigatório
        composicao.setMedidaPrincipio(medidaBasicaPrincipio);   // obrigatório
        // Verificando se os dados do excipiente foram fornecidos antes de atribuir.
        if (this.unidadeMedidaExcipiente != null && !this.unidadeMedidaExcipiente.isEmpty()) {
            MedidaBasica medidaBasicaExcipiente = medidaBasicaRepository.findBySigla(this.unidadeMedidaExcipiente);
            composicao.setMedidaExcipiente(medidaBasicaExcipiente);
            composicao.setQuantiaExcipiente(this.quantiaExcipiente);
        }
        return composicao;
    }

    public ComposicaoInputDTO() {
    }

    public ComposicaoInputDTO(String principioAtivo, float quantiaPrincipio, String unidadeMedidaPrincipio, float quantiaExcipiente, String unidadeMedidaExcipiente) {
        this.principioAtivo = principioAtivo;
        this.quantiaPrincipio = quantiaPrincipio;
        this.unidadeMedidaPrincipio = unidadeMedidaPrincipio;
        this.quantiaExcipiente = quantiaExcipiente;
        this.unidadeMedidaExcipiente = unidadeMedidaExcipiente;
    }

    public ComposicaoInputDTO(String principioAtivo, float quantiaPrincipio, String unidadeMedidaPrincipio) {
        this.principioAtivo = principioAtivo;
        this.quantiaPrincipio = quantiaPrincipio;
        this.unidadeMedidaPrincipio = unidadeMedidaPrincipio;
    }

    public String getPrincipioAtivo() {
        return principioAtivo;
    }

    public void setPrincipioAtivo(String principioAtivo) {
        this.principioAtivo = principioAtivo;
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
}
