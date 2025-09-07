package com.josepacheco.tcc.controller.remedio.formulacao;

import com.josepacheco.tcc.dto.produto.remedio.formulacao.ComposicaoInputDTO;
import com.josepacheco.tcc.dto.produto.remedio.formulacao.ComposicaoOutputDTO;
import com.josepacheco.tcc.service.remedio.formulacao.ComposicaoService;
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
@RequestMapping("/api/produto/remedio/formulacao/composicao")
@Tag(name = "Composições", description = "Endpoints para gerenciamento de Composições de remédios (Princípio ativo e concentração).")
public class ComposicaoController {

    @Autowired
    private ComposicaoService composicaoService;

    @Operation(summary = "Listar todos as composições")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso, lista encontrada no banco de dados"),
            @ApiResponse(responseCode = "204", description = "Consulta realizada com sucesso, porém não há resgistro no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<ComposicaoOutputDTO>> list(){
        try{
            List<ComposicaoOutputDTO> concentracoesOutputDTOs = composicaoService.list();
            if(concentracoesOutputDTOs != null)
                return new ResponseEntity<>(concentracoesOutputDTOs, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Obter detalhes de uma composição pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto encontrado no banco de dados"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComposicaoOutputDTO> detalhes(@PathVariable Long id){
        try{
            ComposicaoOutputDTO concentracaoOutputDTO = composicaoService.getById(id);
            if (concentracaoOutputDTO != null) {
                return new ResponseEntity<>(concentracaoOutputDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Criar uma nova composição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado no banco de dados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Há dados de entrada incorretos, verificar schema e exemplos."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComposicaoOutputDTO> create(@Valid @RequestBody ComposicaoInputDTO concentracaoInputDTO){
        try{
            ComposicaoOutputDTO concentracaoOutputDTO = composicaoService.create(concentracaoInputDTO);
            return new ResponseEntity<>(concentracaoOutputDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Atualizar uma composição existente pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado no banco de dados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComposicaoOutputDTO> update(@PathVariable Long id, @Valid @RequestBody ComposicaoInputDTO concentracaoInputDTO) {
        try {
            ComposicaoOutputDTO concentracaoOutputDTO = composicaoService.update(id, concentracaoInputDTO);
            if (concentracaoOutputDTO != null) {
                return new ResponseEntity<>(concentracaoOutputDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Excluir uma composição pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Objeto excluído do banco de dados com sucesso!"),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado no banco de dados."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
        try {
            if (composicaoService.delete(id)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
