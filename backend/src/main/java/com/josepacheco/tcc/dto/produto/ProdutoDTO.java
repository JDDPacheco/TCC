package com.josepacheco.tcc.dto.produto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.josepacheco.tcc.dto.produto.remedio.GenericoInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.RemedioInputDTO;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.MedidaPadrao;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "tipoProduto", // O campo no seu JSON
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RemedioInputDTO.class, name = "remedio"),
        @JsonSubTypes.Type(value = GenericoInputDTO.class, name = "generico"),
        @JsonSubTypes.Type(value = ProdutoDTO.class, name = "geral")
})

public class ProdutoDTO {

    @NotBlank(message = "O código ean é obrigatório.")
    @Schema(description = "ean, código único de 13 dígitos identificador de cada produto (código de barras)", example = "9780231564891")
    private String ean;

    @NotBlank(message = "O nome comercial do produto é obrigatório.")
    @Schema(description = "nome comercial do produto, nome pelo qual se identifica o produto em notas fiscais", example = "Neosaldina DIP")
    private String nomeComercial;

    @NotBlank(message = "A unidade de medida padrão para o produto é obrigatório.")
    @Schema(description = "sigla da unidade de medida padrão, deve ser obtida da lista em /api/produto/medida_padrao", example = "'CX' para Caixa ou 'UN' para unidade")
    private String unidadeDeMedida;

    @NotBlank(message = "O tipo do produto é obrigatório.")
    @Schema(description = "Especicificar o tipo de produto.", example = "'geral' para produtos não medicamento, 'remedio' para remedios em geral, 'generico' para medicamentos genéricos")
    private String tipoProduto;

    public ProdutoDTO(){}

    public ProdutoDTO(Produto produto){
        this.ean = produto.getEan();
        this.nomeComercial = produto.getNomeComercial();
        this.unidadeDeMedida = produto.getUnidadeDeMedida().getSigla();
        if(!produto.getClass().getSimpleName().equalsIgnoreCase("produto"))
            this.tipoProduto = produto.getClass().getSimpleName().toLowerCase(); // Ex: "remedio", "generico"
        else
            this.tipoProduto = "geral";
    }

    public Produto build(MedidaPadraoRepository medidaPadraoRepository){
        // Resolvendo dependências
        MedidaPadrao medidaPadraoProduto = medidaPadraoRepository.findBySigla(this.unidadeDeMedida);

        // Criando objeto de Concentração
        Produto produto = new Produto();
        produto.setEan(this.ean);
        produto.setNomeComercial(this.nomeComercial);
        produto.setUnidadeDeMedida(medidaPadraoProduto);

        return produto;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getNomeComercial() {
        return nomeComercial;
    }

    public void setNomeComercial(String nomeComercial) {
        this.nomeComercial = nomeComercial;
    }

    public String getUnidadeDeMedida() {
        return unidadeDeMedida;
    }

    public void setUnidadeDeMedida(String unidadeDeMedida) {
        this.unidadeDeMedida = unidadeDeMedida;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipo_produto) {
        this.tipoProduto = tipo_produto;
    }
}
