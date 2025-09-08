package com.josepacheco.tcc.service.produto.remedio.formulacao;

import com.josepacheco.tcc.dto.produto.remedio.formulacao.ComposicaoInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.ComposicaoOutputDTO;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Composicao;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.ComposicaoRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.MedidaBasicaRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.PrincipioAtivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<ComposicaoOutputDTO> list(){
        List<Composicao> composicoes = composicaoRepository.findAll();
        List<ComposicaoOutputDTO> concentracoesDTOs = new ArrayList<>();
        for (Composicao composicao: composicoes){
            concentracoesDTOs.add(new ComposicaoOutputDTO(composicao));
        }
        return concentracoesDTOs;
    }

    public ComposicaoOutputDTO getById(Long id){
        return new ComposicaoOutputDTO(composicaoRepository.getReferenceById(id));
    }

    public ComposicaoOutputDTO create(ComposicaoInputDTO composicaoInputDTO){
        return new ComposicaoOutputDTO(composicaoRepository.save(composicaoInputDTO.build(medidaBasicaRepository, principioAtivoRepository)));
    }

    public ComposicaoOutputDTO update(Long id, ComposicaoInputDTO composicaoInputDTO){
        // Encontrando concentracao no banco de dados
        Composicao composicaoEncontrada = composicaoRepository.getReferenceById(id);

        // Montando nova concentracao com valores novos
        Composicao composicaoNova = composicaoInputDTO.build(medidaBasicaRepository, principioAtivoRepository);

        // Alterando os valores antigos pelo novos
        composicaoEncontrada.setPrincipioAtivo(composicaoNova.getPrincipioAtivo());
        composicaoEncontrada.setQuantiaPrincipio(composicaoNova.getQuantiaPrincipio());
        composicaoEncontrada.setMedidaPrincipio(composicaoNova.getMedidaPrincipio());
        composicaoEncontrada.setQuantiaExcipiente(composicaoNova.getQuantiaExcipiente());
        composicaoEncontrada.setMedidaExcipiente(composicaoNova.getMedidaExcipiente());

        return new ComposicaoOutputDTO(composicaoRepository.save(composicaoEncontrada));
    }

    public boolean delete(Long id){
        Composicao composicao = composicaoRepository.getReferenceById(id);
        if(composicaoRepository.existsById(id)){ // a composicao existe, ela é excluída
            composicaoRepository.delete(composicao);
            return true;
        } else {                  // a composicao não existia, erro 404 NOT.FOUND
            return false;
        }
    }
}
