package com.josepacheco.tcc.service.produto.remedio.atributos;

import com.josepacheco.tcc.dto.produto.remedio.MedidaFarmaceuticaDTO;
import com.josepacheco.tcc.model.produto.remedio.atributos.MedidaFarmaceutica;
import com.josepacheco.tcc.repository.produto.remedio.atributos.MedidaFarmaceuticaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedidaFarmaceuticaService {

    @Autowired
    private MedidaFarmaceuticaRepository unidadeDeMedidaFarmaceuticaRepository;

    public List<MedidaFarmaceuticaDTO> list(){
        List<MedidaFarmaceutica> unidadesDeMedidaFarmaceticas = unidadeDeMedidaFarmaceuticaRepository.findAll();
        List<MedidaFarmaceuticaDTO> unidadesDeMedidaFarmaceuticasOutputDTOs = new ArrayList<>();
        for(MedidaFarmaceutica unidadeDeMedidaFarmacetica: unidadesDeMedidaFarmaceticas){
            unidadesDeMedidaFarmaceuticasOutputDTOs.add(new MedidaFarmaceuticaDTO(unidadeDeMedidaFarmacetica));
        }
        return unidadesDeMedidaFarmaceuticasOutputDTOs;
    }
}
