package com.josepacheco.tcc.service.produto.remedio.atributos;

import com.josepacheco.tcc.dto.produto.remedio.atributos.ApresentacaoDTO;
import com.josepacheco.tcc.model.produto.remedio.atributos.Apresentacao;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ApresentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApresentacaoService {

    @Autowired
    private ApresentacaoRepository apresentacaoRepository;

    public List<ApresentacaoDTO> list(){
        List<Apresentacao> apresentacoes = apresentacaoRepository.findAll();
        List<ApresentacaoDTO> apresentacoesOutputDTOs = new ArrayList<>();
        for(Apresentacao apresentacao: apresentacoes){
            apresentacoesOutputDTOs.add(new ApresentacaoDTO(apresentacao));
        }
        return apresentacoesOutputDTOs;
    }

//    public ApresentacaoDTO getByApresentacao(String apresentacao){
//        return new ApresentacaoDTO(apresentacaoRepository.findByApresentacao(apresentacao));
//    }

//    public ApresentacaoDTO create(ApresentacaoInputDTO apresentacaoInputDTO){
//        return new ApresentacaoDTO(apresentacaoRepository.save(apresentacaoInputDTO.build()));
//    }
//
//    public ApresentacaoDTO update(Long id, String nome){
//        // Encontrando apresentação no banco de dados
//        Apresentacao apresentacao = apresentacaoRepository.getReferenceById(id);
//
//        // Alterando os valores antigos pelo novos
//        apresentacao.setApresentacao(nome);
//
//        return new ApresentacaoDTO(apresentacaoRepository.save(apresentacao));
//    }
//
//    public boolean delete(Long id){
//        Apresentacao apresentacao = apresentacaoRepository.getReferenceById(id);
//        if(apresentacaoRepository.existsById(id)){
//            apresentacaoRepository.delete(apresentacao);
//            return true;
//        } else {
//            return false;
//        }
//    }
}
