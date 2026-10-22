package com.josepacheco.tcc.controller.catalogo;

import com.josepacheco.tcc.dto.catalogo.ItemCatalogoOutputDTO;
import com.josepacheco.tcc.service.catalogo.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@Tag(name = "Catálogo", description = "Visão geral dos produtos para a tela inicial: marca, fórmula, estoque e preço atual.")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    @Operation(summary = "Lista todos os produtos (em ordem de nome) com marca, fórmula, saldo disponível e preço atual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Há produtos cadastrados"),
            @ApiResponse(responseCode = "204", description = "Nenhum produto cadastrado")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemCatalogoOutputDTO>> listar() {
        List<ItemCatalogoOutputDTO> itens = catalogoService.listar();
        return itens.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : ResponseEntity.ok(itens);
    }
}
