package com.josepacheco.tcc.service.produto.remedio.formulacao;

import com.josepacheco.tcc.dto.produto.remedio.formulacao.ComposicaoDTO;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Composicao;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.ComposicaoRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.MedidaBasicaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.PrincipioAtivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComposicaoService {

    @Autowired
    private ComposicaoRepository composicaoRepository;

    @Autowired
    private MedidaBasicaRepository medidaBasicaRepository;

    @Autowired
    private PrincipioAtivoRepository principioAtivoRepository;

    public List<ComposicaoDTO> list(){
        List<Composicao> composicoes = composicaoRepository.findAll();
        List<ComposicaoDTO> concentracoesDTOs = new ArrayList<>();
        for (Composicao composicao: composicoes){
            concentracoesDTOs.add(new ComposicaoDTO(composicao));
        }
        return concentracoesDTOs;
    }

    public ComposicaoDTO getById(Long id){
        return new ComposicaoDTO(buscarPorId(id));
    }

    public ComposicaoDTO create(ComposicaoDTO composicaoDTO){
        return new ComposicaoDTO(composicaoRepository.save(composicaoDTO.build(medidaBasicaRepository, principioAtivoRepository)));
    }

    public ComposicaoDTO update(Long id, ComposicaoDTO composicaoDTO){
        // Encontrando concentracao no banco de dados
        Composicao composicaoEncontrada = buscarPorId(id);

        // Montando nova concentracao com valores novos
        Composicao composicaoNova = composicaoDTO.build(medidaBasicaRepository, principioAtivoRepository);

        // Alterando os valores antigos pelo novos
        composicaoEncontrada.setPrincipioAtivo(composicaoNova.getPrincipioAtivo());
        composicaoEncontrada.setQuantiaPrincipio(composicaoNova.getQuantiaPrincipio());
        composicaoEncontrada.setMedidaPrincipio(composicaoNova.getMedidaPrincipio());
        composicaoEncontrada.setQuantiaExcipiente(composicaoNova.getQuantiaExcipiente());
        composicaoEncontrada.setMedidaExcipiente(composicaoNova.getMedidaExcipiente());

        return new ComposicaoDTO(composicaoRepository.save(composicaoEncontrada));
    }

//    public boolean delete(Long id){
//        Composicao composicao = composicaoRepository.getReferenceById(id);
//        if(composicaoRepository.existsById(id)){ // a composicao existe, ela é excluída
//            composicaoRepository.delete(composicao);
//            return true;
//        } else {                  // a composicao não existia, erro 404 NOT.FOUND
//            return false;
//        }
//    }

    private Composicao buscarPorId(Long id) {
        return composicaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Composição não encontrada com id: " + id));
    }
}
