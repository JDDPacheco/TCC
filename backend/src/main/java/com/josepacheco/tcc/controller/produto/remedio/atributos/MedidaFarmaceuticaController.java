package com.josepacheco.tcc.controller.produto.remedio.atributos;

import com.josepacheco.tcc.dto.produto.remedio.MedidaFarmaceuticaDTO;
import com.josepacheco.tcc.service.produto.remedio.atributos.MedidaFarmaceuticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/produto/remedio/medida_farmaceutica")
@Tag(name = "Listas para consulta simples de atributos de remédio e de suas fórmulas", description = "Endpoints para acessar atributos úteis para a criação de remédios e suas fórmulas.")
public class MedidaFarmaceuticaController {

    @Autowired
    private MedidaFarmaceuticaService medidaFarmaceuticaService;

    @Operation(summary = "Medidas comuns de quantidade de doses na embalagem.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso, lista encontrada no banco de dados"),
            @ApiResponse(responseCode = "204", description = "Consulta realizada com sucesso, porém não há resgistro no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<MedidaFarmaceuticaDTO>> list(){
        try{
            List<MedidaFarmaceuticaDTO> unidadeDeMedidaFarmaceuticaOutputDTOS = medidaFarmaceuticaService.list();
            if(!unidadeDeMedidaFarmaceuticaOutputDTOS.isEmpty())
                return new ResponseEntity<>(unidadeDeMedidaFarmaceuticaOutputDTOS, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
//
//    @Operation(summary = "Obter detalhes de uma unidade de medica farmacêutica pelo id")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Objeto encontrado no banco de dados"),
//            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados"),
//            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
//    })
//    @GetMapping(value = "/{sigla}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<MedidaFarmaceuticaDTO> detalhes(@PathVariable String sigla){
//        try{
//            MedidaFarmaceuticaDTO medidaFarmaceuticaDTO = medidaFarmaceuticaService.getBySigla(sigla);
//            if (medidaFarmaceuticaDTO != null) {
//                return new ResponseEntity<>(medidaFarmaceuticaDTO, HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @Operation(summary = "Criar uma nova unidade de medida farmacêutica.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Objeto criado no banco de dados com sucesso!"),
//            @ApiResponse(responseCode = "400", description = "Há dados de entrada incorretos, verificar schema e exemplos."),
//            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
//    })
//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<MedidaFarmaceuticaDTO> create(@Valid @RequestBody UnidadeDeMedidaFarmaceuticaInputDTO unidadeDeMedidaFarmaceuticaInputDTO){
//        try{
//            MedidaFarmaceuticaDTO unidadeDeMedidaFarmaceuticaOutputDTO = unidadeDeMedidaFarmaceuticaService.create(unidadeDeMedidaFarmaceuticaInputDTO);
//            return new ResponseEntity<>(unidadeDeMedidaFarmaceuticaOutputDTO, HttpStatus.CREATED);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @Operation(summary = "Atualizar uma medida farmacêutica existente pelo id.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Objeto atualizado no banco de dados com sucesso!"),
//            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
//            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
//    })
//    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<MedidaFarmaceuticaDTO> update(@PathVariable Long id, @Valid @RequestBody UnidadeDeMedidaFarmaceuticaInputDTO unidadeDeMedidaFarmaceuticaInputDTO) {
//        try {
//            MedidaFarmaceuticaDTO unidadeDeMedidaFarmaceuticaOutputDTO = unidadeDeMedidaFarmaceuticaService.update(id, unidadeDeMedidaFarmaceuticaInputDTO);
//            if (unidadeDeMedidaFarmaceuticaOutputDTO != null) {
//                return new ResponseEntity<>(unidadeDeMedidaFarmaceuticaOutputDTO, HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @Operation(summary = "Excluir uma apresentação pelo id")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "Objeto excluído do banco de dados com sucesso!"),
//            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
//            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
//    })
//    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
//        try {
//            if (unidadeDeMedidaFarmaceuticaService.delete(id)) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            } else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
