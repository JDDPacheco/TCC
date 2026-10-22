package com.josepacheco.tcc.service.catalogo;

import com.josepacheco.tcc.dto.catalogo.ItemCatalogoOutputDTO;
import com.josepacheco.tcc.dto.estoque.SaldoProdutoOutputDTO;
import com.josepacheco.tcc.model.preco.Preco;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.remedio.Generico;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Composicao;
import com.josepacheco.tcc.repository.preco.PrecoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import com.josepacheco.tcc.service.estoque.EstoqueService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Catálogo da tela inicial: junta produto (marca e fórmula), estoque e preço atual em uma única consulta.
 */
@Service
public class CatalogoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private EstoqueService estoqueService;

    @Transactional(readOnly = true)
    public List<ItemCatalogoOutputDTO> listar() {
        // Preço atual de cada produto (se dois preços tiverem a mesma data de vigência, vale o cadastrado por último)
        BinaryOperator<Preco> maisRecente = (a, b) -> a.getId() > b.getId() ? a : b;
        Map<Long, Preco> precoPorProduto = precoRepository.findPrecosAtuais().stream()
                .collect(Collectors.toMap(p -> p.getProduto().getId(), p -> p, maisRecente));

        Map<String, Integer> saldoPorEan = estoqueService.listarSaldos().stream()
                .collect(Collectors.toMap(SaldoProdutoOutputDTO::getEan, SaldoProdutoOutputDTO::getSaldoDisponivel));

        return produtoRepository.findAll().stream()
                .sorted(Comparator.comparing(Produto::getNomeComercial, String.CASE_INSENSITIVE_ORDER))
                .map(produto -> montarItem(produto, precoPorProduto.get(produto.getId()), saldoPorEan.getOrDefault(produto.getEan(), 0)))
                .toList();
    }

    private ItemCatalogoOutputDTO montarItem(Produto produto, Preco preco, int saldoDisponivel) {
        // O produto pode ser um proxy do Hibernate; o tipo real só é visível depois de "desembrulhá-lo"
        Object real = Hibernate.unproxy(produto);
        String tipoProduto = real instanceof Generico ? "generico" : real instanceof Remedio ? "remedio" : "geral";
        String marca = null;
        String formula = null;
        String tipoControle = null;
        if (real instanceof Remedio remedio) {
            marca = remedio.getLaboratorio() != null ? remedio.getLaboratorio().getMarca() : null;
            formula = remedio.getFormula() != null
                    ? remedio.getFormula().getComposicoes().stream().map(Composicao::toString).collect(Collectors.joining(" + "))
                    : null;
            tipoControle = remedio.getControle() != null ? remedio.getControle().getTipoControle() : null;
        }
        return new ItemCatalogoOutputDTO(produto.getEan(), produto.getNomeComercial(), tipoProduto, marca, formula, tipoControle,
                produto.getUnidadeDeMedida().getSigla(), saldoDisponivel, preco != null ? preco.getValorVenda() : null);
    }
}
