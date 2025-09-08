package com.josepacheco.tcc.controller.produto.remedio.atributos;

import com.josepacheco.tcc.dto.produto.remedio.atributos.ApresentacaoDTO;
import com.josepacheco.tcc.service.produto.remedio.atributos.ApresentacaoService;
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
@RequestMapping("/api/produto/remedio/apresentacao")
@Tag(name = "Listas para consulta simples de atributos de remédio e de suas fórmulas", description = "Endpoints para acessar atributos úteis para a criação de remédios e suas fórmulas.")
public class ApresentacaoController {

    @Autowired
    private ApresentacaoService apresentacaoService;

    @Operation(summary = "Formas de apresentação de remédios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso, lista encontrada no banco de dados"),
            @ApiResponse(responseCode = "204", description = "Consulta realizada com sucesso, porém não há resgistro no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<ApresentacaoDTO>> list(){
        try{
            List<ApresentacaoDTO> apresentacoesOutputDTOs = apresentacaoService.list();
            if(!apresentacoesOutputDTOs.isEmpty())
                return new ResponseEntity<>(apresentacoesOutputDTOs, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

//    @Operation(summary = "Obter detalhes de uma apresentação de medicamento pelo id")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Objeto encontrado no banco de dados"),
//            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados"),
//            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
//    })
//    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApresentacaoDTO> detalhes(@PathVariable Long id){
//        try{
//            ApresentacaoDTO apresentacaoOutputDTO = apresentacaoService.getById(id);
//            if (apresentacaoOutputDTO != null) {
//                return new ResponseEntity<>(apresentacaoOutputDTO, HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @Operation(summary = "Criar uma nova apresentação de medicamento.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Objeto criado no banco de dados com sucesso!"),
//            @ApiResponse(responseCode = "400", description = "Há dados de entrada incorretos, verificar schema e exemplos."),
//            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
//    })
//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApresentacaoDTO> create(@Valid @RequestBody ApresentacaoInputDTO apresentacaoInputDTO){
//        try{
//            ApresentacaoDTO apresentacaoOutputDTO = apresentacaoService.create(apresentacaoInputDTO);
//            return new ResponseEntity<>(apresentacaoOutputDTO, HttpStatus.CREATED);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @Operation(summary = "Atualizar uma apresentação existente pelo id")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Objeto atualizado no banco de dados com sucesso!"),
//            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
//            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
//    })
//    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApresentacaoDTO> update(@PathVariable Long id, @Valid @RequestBody String apresentacaoNome) {
//        try {
//            ApresentacaoDTO apresentacaoOutputDTO = apresentacaoService.update(id, apresentacaoNome);
//            if (apresentacaoOutputDTO != null) {
//                return new ResponseEntity<>(apresentacaoOutputDTO, HttpStatus.OK);
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
//            if (apresentacaoService.delete(id)) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            } else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
