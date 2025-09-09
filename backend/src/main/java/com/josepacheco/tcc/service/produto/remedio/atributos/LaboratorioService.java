package com.josepacheco.tcc.service.produto.remedio.atributos;

import com.josepacheco.tcc.dto.produto.remedio.LaboratorioDTO;
import com.josepacheco.tcc.model.produto.remedio.atributos.Laboratorio;
import com.josepacheco.tcc.repository.produto.remedio.atributos.LaboratorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LaboratorioService {

    @Autowired
    private LaboratorioRepository laboratorioRepository;

    public List<LaboratorioDTO> list(){
        List<Laboratorio> laboratorios = laboratorioRepository.findAll();
        List<LaboratorioDTO> laboratoriosOutputDTOs = new ArrayList<>();
        for(Laboratorio laboratorio: laboratorios){
            laboratoriosOutputDTOs.add(new LaboratorioDTO(laboratorio));
        }
        return laboratoriosOutputDTOs;
    }

    public LaboratorioDTO getById(Long id){
        return new LaboratorioDTO(laboratorioRepository.getReferenceById(id));
    }

    public LaboratorioDTO create(LaboratorioDTO laboratorioDTO){
        return new LaboratorioDTO(laboratorioRepository.save(laboratorioDTO.build()));
    }

    public LaboratorioDTO update(Long id, LaboratorioDTO laboratorioDTO){
        // Encontrando laboratório no banco de dados
        Laboratorio laboratorioEncontrado = laboratorioRepository.getReferenceById(id);

        // Montando novo laboratório com valores novos
        Laboratorio laboratorioNovo = laboratorioDTO.build();

        // Alterando os valores antigos pelo novos
        laboratorioEncontrado.setMarca(laboratorioNovo.getMarca());
        laboratorioEncontrado.setNomeFantasia(laboratorioNovo.getNomeFantasia());

        return new LaboratorioDTO(laboratorioRepository.save(laboratorioEncontrado));
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
