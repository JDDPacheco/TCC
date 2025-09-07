package com.josepacheco.tcc.service.remedio.formulacao;

import com.josepacheco.tcc.dto.produto.remedio.formulacao.PrincipioAtivoInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.PrincipioAtivoOutputDTO;
import com.josepacheco.tcc.model.produto.remedio.formulacao.PrincipioAtivo;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.PrincipioAtivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrincipioAtivoService {

    @Autowired
    private PrincipioAtivoRepository principioAtivoRepository;

    public List<PrincipioAtivoOutputDTO> list(){
        List<PrincipioAtivo> principiosAtivos = principioAtivoRepository.findAll();
        List<PrincipioAtivoOutputDTO> principiosAtivosDTOs = new ArrayList<>();
        for(PrincipioAtivo principioAtivo: principiosAtivos){
            principiosAtivosDTOs.add(new PrincipioAtivoOutputDTO(principioAtivo));
        }
        return principiosAtivosDTOs;
    }

    public PrincipioAtivoOutputDTO getById(Long id){
        return new PrincipioAtivoOutputDTO(principioAtivoRepository.getReferenceById(id));
    }

    public PrincipioAtivoOutputDTO create(PrincipioAtivoInputDTO principioAtivoDTO){
        return new PrincipioAtivoOutputDTO(principioAtivoRepository.save(principioAtivoDTO.build()));
    }

    public PrincipioAtivoOutputDTO update(Long id, String nome){
        PrincipioAtivo principioAtivo = principioAtivoRepository.getReferenceById(id);
        principioAtivo.setNome(nome);
        return new PrincipioAtivoOutputDTO(principioAtivoRepository.save(principioAtivo));
    }

    public boolean delete(Long id){
        PrincipioAtivo principioAtivo = principioAtivoRepository.getReferenceById(id);
        if(principioAtivoRepository.existsById(id)){
            principioAtivoRepository.delete(principioAtivo);
            return true;
        } else {
            return false;
        }
    }
}
