package com.josepacheco.tcc.controller.remedio.formulacao;

import com.josepacheco.tcc.dto.produto.remedio.formulacao.MedidaBasicaInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.MedidaBasicaOutputDTO;
import com.josepacheco.tcc.service.remedio.formulacao.MedidaBasicaService;
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
@RequestMapping("/api/produto/remedio/formulacao/medida_basica")
@Tag(name = "Medidas Básicas", description = "Endpoints para gerenciamento das unidades de medida do Sistema Internacional.")
public class MedidaBasicaController {

    @Autowired
    private MedidaBasicaService medidaBasicaService;

    @Operation(summary = "Listar todas as medidas básicas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso, lista encontrada no banco de dados"),
            @ApiResponse(responseCode = "204", description = "Consulta realizada com sucesso, porém não há resgistro no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<MedidaBasicaOutputDTO>> list(){
        try{
            List<MedidaBasicaOutputDTO> medidaBasicaDTOs = medidaBasicaService.list();
            if(!medidaBasicaDTOs.isEmpty())
                return new ResponseEntity<>(medidaBasicaDTOs, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Obter detalhes de uma medida básica pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto encontrado no banco de dados"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedidaBasicaOutputDTO> detalhes(@PathVariable Long id){
        try{
            MedidaBasicaOutputDTO medidaBasicaOutputDTO = medidaBasicaService.getById(id);
            if (medidaBasicaOutputDTO != null) {
                return new ResponseEntity<>(medidaBasicaOutputDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Criar uma nova medida básica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado no banco de dados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Há dados de entrada incorretos, verificar schema e exemplos."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedidaBasicaOutputDTO> create(@Valid @RequestBody MedidaBasicaInputDTO medidaBasicaDTO){
        try{
            MedidaBasicaOutputDTO createdMedidaBasica = medidaBasicaService.create(medidaBasicaDTO);
            return new ResponseEntity<>(createdMedidaBasica, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Atualizar uma medida básica existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado no banco de dados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MedidaBasicaOutputDTO> update(@PathVariable Long id, @Valid @RequestBody MedidaBasicaInputDTO medidaBasicaInputDTO){
        try {
            MedidaBasicaOutputDTO medidaBasicaOutputDTO = medidaBasicaService.update(id, medidaBasicaInputDTO);
            if (medidaBasicaOutputDTO != null) {
                return new ResponseEntity<>(medidaBasicaOutputDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Excluir uma medida básica pela sigla")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Objrto excluído do banco de dados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
        try {
            if (medidaBasicaService.delete(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
