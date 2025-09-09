package com.josepacheco.tcc.dto.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.Composicao;
import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import com.josepacheco.tcc.model.produto.remedio.formulacao.PrincipioAtivo;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.MedidaBasicaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.PrincipioAtivoRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

public class ComposicaoDTO {

    @Null(message = "O id deve ser omitido na criação e atualização.")
    @Schema(description = "código id da concentracao, é a chave. (deve ser null ou omitido na criação e atualização)", example = "1")
    private Long id;

    @NotBlank(message = "O nome do princípio ativo é obrigatório.")
    @Schema(description = "nome do princípio ativo", example = "dipirona")
    private String principioAtivo;

    @NotBlank(message = "A quantia de princípio ativo é obrigatória.")
    @Schema(description = "quantia de princípio ativo base", example = "300")
    private float quantiaPrincipio;

    @NotBlank(message = "A unidade de medida de princípio ativo é obrigatória.")
    @Schema(description = "unidade de medida de princípio ativo", example = "mg")
    private String unidadeMedidaPrincipio;

    @Schema(description = "quantia de excipiente (deixar em branco quando for por comprimido ou dose)", example = "5")
    private float quantiaExcipiente;

    @Schema(description = "unidade de medida do excipiente (deixar em branco quando for compirmido ou dose)", example = "ml")
    private String unidadeMedidaExcipiente;

    public ComposicaoDTO() {
    }

    public ComposicaoDTO(Composicao composicao){
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

    public Composicao build(MedidaBasicaRepository medidaBasicaRepository, PrincipioAtivoRepository principioAtivoRepository){
        // Resolvendo dependências
        PrincipioAtivo principioAtivo = principioAtivoRepository.findByNome(this.principioAtivo);
        MedidaBasica medidaBasicaPrincipio = medidaBasicaRepository.findBySigla(this.unidadeMedidaPrincipio);

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
