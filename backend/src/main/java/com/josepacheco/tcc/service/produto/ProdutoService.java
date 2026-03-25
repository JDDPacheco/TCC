package com.josepacheco.tcc.service.produto;

import com.josepacheco.tcc.dto.produto.ProdutoDTO;
import com.josepacheco.tcc.dto.produto.remedio.GenericoInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.GenericoOutputDTO;
import com.josepacheco.tcc.dto.produto.remedio.RemedioInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.RemedioOutputDTO;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.model.produto.remedio.Generico;
import com.josepacheco.tcc.model.produto.remedio.Remedio;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ApresentacaoRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ControleReceitaRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.LaboratorioRepository;
import com.josepacheco.tcc.repository.produto.remedio.atributos.MedidaFarmaceuticaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.FormulaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.MedidaBasicaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<ProdutoDTO> list() {
        return produtoRepository.findAll().stream()
                .map(this::convertToOutputDTO) // Usa o metodo auxiliar de conversão
                .collect(Collectors.toList());
    }

    public ProdutoDTO getByEan(String ean) {
        Produto produto = produtoRepository.findByEan(ean)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Produto não encontrado com EAN: " + ean));
        return convertToOutputDTO(produto);
    }

    @Transactional // Garante que todas as operações ocorram em uma única transação
    public ProdutoDTO create(ProdutoDTO produtoDTO) {
        // Verifica se já existe produto com o mesmo EAN
        produtoRepository.findByEan(produtoDTO.getEan()).ifPresent(p -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe um produto com o EAN: " + produtoDTO.getEan());
        });
        Produto produto;
        if (produtoDTO instanceof GenericoInputDTO genericoInputDTO) {
            produto = produtoRepository.save(genericoInputDTO.build(formulaRepository, laboratorioRepository, medidaFarmaceuticaRepository, medidaBasicaRepository, apresentacaoRepository, medidaPadraoRepository, controleReceitaRepository));
        } else if (produtoDTO instanceof RemedioInputDTO remedioInputDTO) {
            produto = produtoRepository.save(remedioInputDTO.build(formulaRepository, laboratorioRepository, medidaFarmaceuticaRepository, medidaBasicaRepository, apresentacaoRepository, medidaPadraoRepository, controleReceitaRepository));
        } else {
            produto = produtoRepository.save(produtoDTO.build(medidaPadraoRepository));
        }
        return convertToOutputDTO(produto);
    }

    @Transactional
    public ProdutoDTO update(String ean, ProdutoDTO produtoDTO) {
        Produto produtoEncontrado = produtoRepository.findByEan(ean)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Produto não encontrado com EAN: " + ean));

        // Valida se o tipo do DTO corresponde ao tipo da entidade existente
        if ((produtoDTO instanceof GenericoInputDTO && !(produtoEncontrado instanceof Generico)) ||
                (produtoDTO instanceof RemedioInputDTO && !(produtoEncontrado instanceof Remedio)) ||
                (!(produtoDTO instanceof RemedioInputDTO) && (produtoEncontrado instanceof Remedio))) { // Verifica se é ProdutoDTO e a entidade é Remedio/Generico
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Não é possível alterar o tipo fundamental do produto (ex: de Produto geral para Remédio).");
        }

        // Montando novo produto com valores novos
        Produto produtoNovo = produtoDTO.build(medidaPadraoRepository);

        // Alterando os valores antigos pelo novos
        produtoEncontrado.setUnidadeDeMedida(produtoNovo.getUnidadeDeMedida());
        produtoEncontrado.setNomeComercial(produtoNovo.getNomeComercial());

        // Atualiza campos específicos se for Remédio ou Genérico
        if (produtoEncontrado instanceof Remedio remedioEncontrado && produtoDTO instanceof RemedioInputDTO remedioInputDTO) {
            Remedio remedioNovo = remedioInputDTO.build(formulaRepository, laboratorioRepository, medidaFarmaceuticaRepository, medidaBasicaRepository, apresentacaoRepository, medidaPadraoRepository, controleReceitaRepository);
            remedioEncontrado.setFormula(remedioNovo.getFormula());
            remedioEncontrado.setLaboratorio(remedioNovo.getLaboratorio());
            remedioEncontrado.setQuantidadeDoses(remedioNovo.getQuantidadeDoses());
            remedioEncontrado.setMedidaDoses(remedioNovo.getMedidaDoses());
            remedioEncontrado.setConteudo(remedioNovo.getConteudo());
            remedioEncontrado.setMedidaConteudo(remedioNovo.getMedidaConteudo());
            remedioEncontrado.setPesoLiquido(remedioNovo.getPesoLiquido());
            remedioEncontrado.setMedidaPeso(remedioNovo.getMedidaPeso());
            remedioEncontrado.setApresentacao(remedioNovo.getApresentacao());
            remedioEncontrado.setControle(remedioNovo.getControle());
        }

        Produto produtoAtualizado = produtoRepository.save(produtoEncontrado);
        return convertToOutputDTO(produtoAtualizado);
    }

    /** Métodos auxiliares */
    private ProdutoDTO convertToOutputDTO(Produto produto) {
        if (produto instanceof Generico g) {
            return new GenericoOutputDTO(g);
        } else if (produto instanceof Remedio r) {
            return new RemedioOutputDTO(r);
        } else {
            return new ProdutoDTO(produto); // Para produtos gerais
        }
    }
}
