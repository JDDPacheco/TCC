package com.josepacheco.tcc.service.produto;

import com.josepacheco.tcc.dto.produto.MedidaPadraoDTO;
import com.josepacheco.tcc.model.produto.MedidaPadrao;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedidaPadraoService {

    @Autowired
    private MedidaPadraoRepository medidaPadraoRepository;

    public List<MedidaPadraoDTO> list(){
        List<MedidaPadrao> medidasPadrao = medidaPadraoRepository.findAll();
        List<MedidaPadraoDTO> medidasPadraoDTOs = new ArrayList<>();
        for (MedidaPadrao medidaPadrao: medidasPadrao){
            medidasPadraoDTOs.add(new MedidaPadraoDTO(medidaPadrao));
        }
        return medidasPadraoDTOs;
    }
}
