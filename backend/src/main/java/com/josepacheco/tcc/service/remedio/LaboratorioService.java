package com.josepacheco.tcc.service.remedio;

import com.josepacheco.tcc.dto.produto.remedio.LaboratorioInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.LaboratorioOutputDTO;
import com.josepacheco.tcc.model.produto.remedio.Laboratorio;
import com.josepacheco.tcc.repository.produto.remedio.LaboratorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LaboratorioService {

    @Autowired
    private LaboratorioRepository laboratorioRepository;

    public List<LaboratorioOutputDTO> list(){
        List<Laboratorio> laboratorios = laboratorioRepository.findAll();
        List<LaboratorioOutputDTO> laboratoriosOutputDTOs = new ArrayList<>();
        for(Laboratorio laboratorio: laboratorios){
            laboratoriosOutputDTOs.add(new LaboratorioOutputDTO(laboratorio));
        }
        return laboratoriosOutputDTOs;
    }

    public LaboratorioOutputDTO getById(Long id){
        return new LaboratorioOutputDTO(laboratorioRepository.getReferenceById(id));
    }

    public LaboratorioOutputDTO create(LaboratorioInputDTO laboratorioInputDTO){
        return new LaboratorioOutputDTO(laboratorioRepository.save(laboratorioInputDTO.build()));
    }

    public LaboratorioOutputDTO update(Long id, LaboratorioInputDTO laboratorioInputDTO){
        // Encontrando laboratório no banco de dados
        Laboratorio laboratorioEncontrado = laboratorioRepository.getReferenceById(id);

        // Montando novo laboratório com valores novos
        Laboratorio laboratorioNovo = laboratorioInputDTO.build();

        // Alterando os valores antigos pelo novos
        laboratorioEncontrado.setMarca(laboratorioNovo.getMarca());
        laboratorioEncontrado.setNomeFantasia(laboratorioNovo.getNomeFantasia());

        return new LaboratorioOutputDTO(laboratorioRepository.save(laboratorioEncontrado));
    }

    public boolean delete(Long id){
        Laboratorio laboratorio = laboratorioRepository.getReferenceById(id);
        if(laboratorioRepository.existsById(id)){
            laboratorioRepository.delete(laboratorio);
            return true;
        } else {
            return false;
        }
    }
}
