package com.josepacheco.tcc.controller.produto.remedio.formulacao;

import com.josepacheco.tcc.dto.produto.remedio.formulacao.PrincipioAtivoDTO;
import com.josepacheco.tcc.service.produto.remedio.formulacao.PrincipioAtivoService;
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
@RequestMapping("/api/produto/remedio/formulacao/principio_ativo")
@Tag(name = "Princípios Ativos", description = "Endpoints para gerenciamento de Princípios Ativos de medicamentos.")
public class PrincipioAtivoController {

    @Autowired
    private PrincipioAtivoService principioAtivoService;

    @Operation(summary = "Listar todos os princípios ativos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso, lista encontrada no banco de dados"),
            @ApiResponse(responseCode = "204", description = "Consulta realizada com sucesso, porém não há resgistro no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<PrincipioAtivoDTO>> list(){
        try{
            List<PrincipioAtivoDTO> principiosAtivosDTOs = principioAtivoService.list();
            if(!principiosAtivosDTOs.isEmpty())
                return new ResponseEntity<>(principiosAtivosDTOs, HttpStatus.OK);
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
    public ResponseEntity<PrincipioAtivoDTO> detalhes(@PathVariable Long id){
        try{
            PrincipioAtivoDTO medidaBasicaOutputDTO = principioAtivoService.getById(id);
            if (medidaBasicaOutputDTO != null) {
                return new ResponseEntity<>(medidaBasicaOutputDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Criar um novo princípio ativo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado no banco de dados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Há dados de entrada incorretos, verificar schema e exemplos."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrincipioAtivoDTO> create(@Valid @RequestBody PrincipioAtivoDTO principioAtivoDTO){
        try{
            PrincipioAtivoDTO createdPrincipioAtivo = principioAtivoService.create(principioAtivoDTO);
            return new ResponseEntity<>(createdPrincipioAtivo, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Atualizar um princípio ativo existente pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado no banco de dados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrincipioAtivoDTO> update(@PathVariable Long id, @Valid @RequestBody String principioNome) {
        try {
            PrincipioAtivoDTO principioAtivoDTO = principioAtivoService.update(id, principioNome);
            if (principioAtivoDTO != null) {
                return new ResponseEntity<>(principioAtivoDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Excluir um princípio ativo pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Objeto excluído do banco de dados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
        try {
            if (principioAtivoService.delete(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
