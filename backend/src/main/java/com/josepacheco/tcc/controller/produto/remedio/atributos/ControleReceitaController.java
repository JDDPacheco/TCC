package com.josepacheco.tcc.controller.produto.remedio.atributos;

import com.josepacheco.tcc.dto.produto.remedio.atributos.ControleReceitaDTO;
import com.josepacheco.tcc.service.produto.remedio.atributos.ControleReceitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/produto/remedio/receita")
@Tag(name = "Listas para consulta simples de atributos de remédio e de suas fórmulas", description = "Endpoints para acessar atributos úteis para a criação de remédios e suas fórmulas.")
public class ControleReceitaController {
    @Autowired
    private ControleReceitaService controleReceitaService;

    @Operation(summary = "Tipos de controle de venda dos medicamentos de acordo com a receita.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso, lista encontrada no banco de dados"),
            @ApiResponse(responseCode = "204", description = "Consulta realizada com sucesso, porém não há resgistro no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<ControleReceitaDTO>> list(){
        try{
            List<ControleReceitaDTO> controlesReceitaOutputDTOs = controleReceitaService.list();
            if(!controlesReceitaOutputDTOs.isEmpty())
                return new ResponseEntity<>(controlesReceitaOutputDTOs, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
