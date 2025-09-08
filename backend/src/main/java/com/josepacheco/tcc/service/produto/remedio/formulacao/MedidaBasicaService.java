package com.josepacheco.tcc.service.produto.remedio.formulacao;

//import com.josepacheco.tcc.dto.produto.remedio.formulacao.MedidaBasicaInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.MedidaBasicaDTO;
import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.MedidaBasicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedidaBasicaService {

    @Autowired
    private MedidaBasicaRepository medidaBasicaRepository;

    public List<MedidaBasicaDTO> list(){
        List<MedidaBasica> medidaBasicas = medidaBasicaRepository.findAll();
        List<MedidaBasicaDTO> medidaBasicaDTOs = new ArrayList<>();
        for (MedidaBasica medidaBasica: medidaBasicas){
            medidaBasicaDTOs.add(new MedidaBasicaDTO(medidaBasica));
        }
        return medidaBasicaDTOs;
    }

//    public MedidaBasicaOutputDTO create(MedidaBasicaInputDTO medidaBasicaDTO){
//            return new MedidaBasicaOutputDTO(medidaBasicaRepository.save(medidaBasicaDTO.build()));
//    }
//
//    public MedidaBasicaOutputDTO update(Long id, MedidaBasicaInputDTO medidaBasicaInputDTO){
//        // Encontrando medida básica no banco de dados
//        MedidaBasica medidaBasicaEncontrada = medidaBasicaRepository.getReferenceById(id);
//
//        // Montando nova medida básica com valores novos
//        MedidaBasica medidaBasicaNova = medidaBasicaInputDTO.build();
//
//        // Alterando os valores antigos pelo novos
//        medidaBasicaEncontrada.setNome(medidaBasicaNova.getNome());
//        medidaBasicaEncontrada.setSigla(medidaBasicaNova.getSigla());
//
//        return new MedidaBasicaOutputDTO(medidaBasicaRepository.save(medidaBasicaEncontrada));
//
//    }
//
//    public boolean delete(Long id){
//        MedidaBasica medidaBasica = medidaBasicaRepository.getReferenceById(id);
//        if(medidaBasicaRepository.existsById(id)){ // a medida existe, ela é excluída
//            medidaBasicaRepository.delete(medidaBasica);
//            return true;
//        } else {                  // a medida não existia, erro 404 NOT.FOUND
//            return false;
//        }
//    }
}
