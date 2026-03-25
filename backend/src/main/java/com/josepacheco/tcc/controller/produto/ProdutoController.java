package com.josepacheco.tcc.controller.produto;

import com.josepacheco.tcc.dto.produto.ProdutoDTO;
import com.josepacheco.tcc.dto.produto.remedio.LaboratorioDTO;
import com.josepacheco.tcc.service.produto.ProdutoService;
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
@RequestMapping("/api/produto")
@Tag(name = "Produtos", description = "Endpoints para gestão de produtos.")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Operation(summary = "Produtos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso, produtos encontrados no banco de dados"),
            @ApiResponse(responseCode = "204", description = "Consulta realizada com sucesso, porém não há registro no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> list(){
        try{
            List<ProdutoDTO> produtosDTO = produtoService.list();
            if(!produtosDTO.isEmpty())
                return new ResponseEntity<>(produtosDTO, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Criar um novo produto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado no banco de dados com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Há dados de entrada incorretos, verificar schema e exemplos."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProdutoDTO> create(@Valid @RequestBody ProdutoDTO produtoDTO){
        try{
            ProdutoDTO produtoDTOCriado = produtoService.create(produtoDTO);
            return new ResponseEntity<>(produtoDTOCriado, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
