package com.josepacheco.tcc.service.produto.remedio.formulacao;

import com.josepacheco.tcc.dto.produto.remedio.formulacao.FormulaInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.FormulaOutputDTO;
import com.josepacheco.tcc.model.produto.remedio.formulacao.Formula;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.ComposicaoRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.FormulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FormulaService {

    @Autowired
    private FormulaRepository formulaRepository;

    @Autowired
    private ComposicaoRepository composicaoRepository;

    public List<FormulaOutputDTO> list(){
        List<Formula> formulas = formulaRepository.findAll();
        List<FormulaOutputDTO> formulasOutputDTOs = new ArrayList<>();
        for (Formula formula: formulas){
            formulasOutputDTOs.add(new FormulaOutputDTO(formula));
        }
        return formulasOutputDTOs;
    }

    public FormulaOutputDTO getById(Long id){
        return new FormulaOutputDTO(formulaRepository.getReferenceById(id));
    }

    public FormulaOutputDTO create(FormulaInputDTO formulaInputDTO){
        return new FormulaOutputDTO(formulaRepository.save(formulaInputDTO.build(composicaoRepository)));
    }

    public FormulaOutputDTO update(Long id, FormulaInputDTO formulaInputDTO){
        // Encontrando formula no banco de dados
        Formula formulaEncontrada = formulaRepository.getReferenceById(id);

        // Montando nova formula com valores novos
        Formula formulaNova = formulaInputDTO.build(composicaoRepository);

        // Alterando os valores antigos pelo novos
        formulaEncontrada.setComposicoes(formulaNova.getComposicoes());
        return new FormulaOutputDTO(formulaRepository.save(formulaEncontrada));
    }

    public boolean delete(Long id){
        Formula formula = formulaRepository.getReferenceById(id);
        if(formulaRepository.existsById(id)){ // a formula existe, ela é excluída
            formulaRepository.delete(formula);
            return true;
        } else {                  // a formula não existia, erro 404 NOT.FOUND
            return false;
        }
    }
}
