package com.josepacheco.tcc.service.produto.remedio.atributos;

import com.josepacheco.tcc.dto.produto.remedio.atributos.ControleReceitaDTO;
import com.josepacheco.tcc.model.produto.remedio.atributos.ControleReceita;
import com.josepacheco.tcc.repository.produto.remedio.atributos.ControleReceitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ControleReceitaService {

    @Autowired
    private ControleReceitaRepository controleReceitaRepository;

    public List<ControleReceitaDTO> list(){
        List<ControleReceita> controlesReceita = controleReceitaRepository.findAll();
        List<ControleReceitaDTO> controlesReceitaOutputDTOs = new ArrayList<>();
        for(ControleReceita controleReceita: controlesReceita){
            controlesReceitaOutputDTOs.add(new ControleReceitaDTO(controleReceita));
        }
        return controlesReceitaOutputDTOs;
    }
}
