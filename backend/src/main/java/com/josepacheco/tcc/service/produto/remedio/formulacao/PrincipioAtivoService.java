package com.josepacheco.tcc.service.produto.remedio.formulacao;

import com.josepacheco.tcc.dto.produto.remedio.formulacao.PrincipioAtivoDTO;
import com.josepacheco.tcc.model.produto.remedio.formulacao.PrincipioAtivo;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.PrincipioAtivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrincipioAtivoService {

    @Autowired
    private PrincipioAtivoRepository principioAtivoRepository;

    public List<PrincipioAtivoDTO> list(){
        List<PrincipioAtivo> principiosAtivos = principioAtivoRepository.findAll();
        List<PrincipioAtivoDTO> principiosAtivosDTOs = new ArrayList<>();
        for(PrincipioAtivo principioAtivo: principiosAtivos){
            principiosAtivosDTOs.add(new PrincipioAtivoDTO(principioAtivo));
        }
        return principiosAtivosDTOs;
    }

    public PrincipioAtivoDTO getById(Long id){
        return new PrincipioAtivoDTO(buscarPorId(id));
    }

    public PrincipioAtivoDTO create(PrincipioAtivoDTO principioAtivoDTO){
        return new PrincipioAtivoDTO(principioAtivoRepository.save(principioAtivoDTO.build()));
    }

    public PrincipioAtivoDTO update(Long id, String nome){
        PrincipioAtivo principioAtivo = buscarPorId(id);
        principioAtivo.setNome(nome);
        return new PrincipioAtivoDTO(principioAtivoRepository.save(principioAtivo));
    }

//    public boolean delete(Long id){
//        PrincipioAtivo principioAtivo = principioAtivoRepository.getReferenceById(id);
//        if(principioAtivoRepository.existsById(id)){
//            principioAtivoRepository.delete(principioAtivo);
//            return true;
//        } else {
//            return false;
//        }
//    }

    private PrincipioAtivo buscarPorId(Long id) {
        return principioAtivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Princípio ativo não encontrado com id: " + id));
    }
}
