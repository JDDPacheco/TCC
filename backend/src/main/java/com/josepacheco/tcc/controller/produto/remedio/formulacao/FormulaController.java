package com.josepacheco.tcc.controller.produto.remedio.formulacao;

import com.josepacheco.tcc.dto.produto.remedio.formulacao.FormulaInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.FormulaOutputDTO;
import com.josepacheco.tcc.service.produto.remedio.formulacao.FormulaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/produto/remedio/formulacao/formula")
@Tag(name = "Fórmulas", description = "Endpoints para gerenciamento de Fórmulas de remédios (conjunto de princípios ativos e suas concentrações).")
public class FormulaController {

    @Autowired
    private FormulaService formulaService;

    @Operation(summary = "Listar todos as fórmulas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso, lista encontrada no banco de dados"),
            @ApiResponse(responseCode = "204", description = "Consulta realizada com sucesso, porém não há resgistro no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<FormulaOutputDTO>> list(){
        try{
            List<FormulaOutputDTO> formulasOutputDTOs = formulaService.list();
            if(!formulasOutputDTOs.isEmpty())
                return new ResponseEntity<>(formulasOutputDTOs, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Obter detalhes de uma fórmula pelo id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto encontrado no banco de dados"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FormulaOutputDTO> detalhes(@PathVariable Long id){
        try{
            FormulaOutputDTO formulaOutputDTO = formulaService.getById(id);
            if (formulaOutputDTO != null) {
                return new ResponseEntity<>(formulaOutputDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Criar uma nova fórmula.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado no banco de dados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Há dados de entrada incorretos, verificar schema e exemplos."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FormulaOutputDTO> create(@Valid @RequestBody FormulaInputDTO formulaInputDTO){
        try{
            FormulaOutputDTO concentracaoOutputDTO = formulaService.create(formulaInputDTO);
            return new ResponseEntity<>(concentracaoOutputDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Atualizar uma fórmula existente pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado no banco de dados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FormulaOutputDTO> update(@PathVariable Long id, @Valid @RequestBody FormulaInputDTO formulaInputDTO) {
        try {
            FormulaOutputDTO formulaOutputDTO = formulaService.update(id, formulaInputDTO);
            if (formulaOutputDTO != null) {
                return new ResponseEntity<>(formulaOutputDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @Operation(summary = "Excluir uma fórmula pelo id")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "Objeto excluído do banco de dados com sucesso!"),
//            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
//            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
//    })
//    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
//        try {
//            if (formulaService.delete(id)) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            } else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
