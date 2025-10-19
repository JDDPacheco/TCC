package com.josepacheco.tcc.service.produto;

import com.josepacheco.tcc.dto.produto.ProdutoDTO;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ApresentacaoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ControleReceitaRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.LaboratorioRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.MedidaFarmaceuticaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.FormulaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.MedidaBasicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MedidaPadraoRepository medidaPadraoRepository;
    @Autowired
    private ApresentacaoRepository apresentacaoRepository;
    @Autowired
    private ControleReceitaRepository controleReceitaRepository;
    @Autowired
    private LaboratorioRepository laboratorioRepository;
    @Autowired
    private MedidaFarmaceuticaRepository medidaFarmaceuticaRepository;
    @Autowired
    private FormulaRepository formulaRepository;
    @Autowired
    private MedidaBasicaRepository medidaBasicaRepository;

    public List<ProdutoDTO> list(){
        List<Produto> produtos = produtoRepository.findAll();
        List<ProdutoDTO> produtosDTO = new ArrayList<>();
        for (Produto produto: produtos){
            produtosDTO.add(new ProdutoDTO(produto));
        }
        return produtosDTO;
    }
}
