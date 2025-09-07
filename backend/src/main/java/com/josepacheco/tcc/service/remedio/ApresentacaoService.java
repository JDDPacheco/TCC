package com.josepacheco.tcc.service.remedio;

import com.josepacheco.tcc.dto.produto.remedio.ApresentacaoInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.ApresentacaoOutputDTO;
import com.josepacheco.tcc.model.produto.remedio.Apresentacao;
import com.josepacheco.tcc.repository.produto.remedio.ApresentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApresentacaoService {

    @Autowired
    private ApresentacaoRepository apresentacaoRepository;

    public List<ApresentacaoOutputDTO> list(){
        List<Apresentacao> apresentacoes = apresentacaoRepository.findAll();
        List<ApresentacaoOutputDTO> apresentacoesOutputDTOs = new ArrayList<>();
        for(Apresentacao apresentacao: apresentacoes){
            apresentacoesOutputDTOs.add(new ApresentacaoOutputDTO(apresentacao));
        }
        return apresentacoesOutputDTOs;
    }

    public ApresentacaoOutputDTO getById(Long id){
        return new ApresentacaoOutputDTO(apresentacaoRepository.getReferenceById(id));
    }

    public ApresentacaoOutputDTO create(ApresentacaoInputDTO apresentacaoInputDTO){
        return new ApresentacaoOutputDTO(apresentacaoRepository.save(apresentacaoInputDTO.build()));
    }

    public ApresentacaoOutputDTO update(Long id, String nome){
        // Encontrando apresentação no banco de dados
        Apresentacao apresentacao = apresentacaoRepository.getReferenceById(id);

        // Alterando os valores antigos pelo novos
        apresentacao.setApresentacao(nome);

        return new ApresentacaoOutputDTO(apresentacaoRepository.save(apresentacao));
    }

    public boolean delete(Long id){
        Apresentacao apresentacao = apresentacaoRepository.getReferenceById(id);
        if(apresentacaoRepository.existsById(id)){
            apresentacaoRepository.delete(apresentacao);
            return true;
        } else {
            return false;
        }
    }
}
