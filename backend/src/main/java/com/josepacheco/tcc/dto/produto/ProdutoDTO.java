package com.josepacheco.tcc.dto.produto;

import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.MedidaPadrao;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

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

    @NotBlank(message = "Especicificar o tipo de produto: [em branco] para produtos não medicamento, 'remedio' para remedios em geral, 'generico' para medicamentos genéricos")
    private String tipoProduto;

    public ProdutoDTO(){}

    public ProdutoDTO(Produto produto){
        this.ean = produto.getEan();
        this.nomeComercial = produto.getNomeComercial();
        this.unidadeDeMedida = produto.getUnidadeDeMedida().getSigla();
        this.tipoProduto = produto.getClass().getSimpleName().toLowerCase(); // Ex: "produto", "remedio", "generico"
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

    public String getTipoProdutoroduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipo_produto) {
        this.tipoProduto = tipo_produto;
    }
}
