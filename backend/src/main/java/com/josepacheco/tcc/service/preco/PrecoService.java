package com.josepacheco.tcc.service.preco;

import com.josepacheco.tcc.dto.preco.PrecoInputDTO;
import com.josepacheco.tcc.dto.preco.PrecoOutputDTO;
import com.josepacheco.tcc.model.preco.Preco;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.repository.preco.PrecoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrecoService {

    @Autowired
    private PrecoRepository precoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public PrecoOutputDTO create(PrecoInputDTO precoInputDTO) {

        Produto produto = produtoRepository.findByEan(precoInputDTO.getEan())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado com EAN: " + precoInputDTO.getEan()));

        if (precoInputDTO.getValorVenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor de venda deve ser maior que zero.");
        }

        Preco preco = new Preco(produto, precoInputDTO.getValorVenda(), LocalDateTime.now());

        Preco precoSalvo = precoRepository.save(preco);

        return new PrecoOutputDTO(precoSalvo);
    }

    public PrecoOutputDTO getPrecoAtual(String ean) {

        return precoRepository
                .findTopByProdutoEanOrderByDataInicioVigenciaDesc(ean)
                .map(PrecoOutputDTO::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum preço encontrado para o produto com EAN: " + ean));
    }

    public List<PrecoOutputDTO> getHistorico(String ean) {

        if (produtoRepository.findByEan(ean).isEmpty()) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Produto não encontrado com EAN: " + ean);
        }

        return precoRepository
                .findByProdutoEanOrderByDataInicioVigenciaDesc(ean)
                .stream()
                .map(PrecoOutputDTO::new)
                .toList();
    }
}