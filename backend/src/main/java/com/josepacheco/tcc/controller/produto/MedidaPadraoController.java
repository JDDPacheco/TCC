package com.josepacheco.tcc.controller.produto;

import com.josepacheco.tcc.dto.produto.MedidaPadraoDTO;
import com.josepacheco.tcc.service.produto.MedidaPadraoService;
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
@RequestMapping("/api/produto/medida_padrao")
@Tag(name = "Listas para consulta simples de atributos de produtos", description = "Endpoints para acessar atributos úteis para a criação de produtos.")
public class MedidaPadraoController {

    @Autowired
    private MedidaPadraoService medidaPadraoService;

    @Operation(summary = "Medidas padrão para produtos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso, lista encontrada no banco de dados"),
            @ApiResponse(responseCode = "204", description = "Consulta realizada com sucesso, porém não há resgistro no banco de dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<List<MedidaPadraoDTO>> list(){
        try{
            List<MedidaPadraoDTO> medidasPadraoDTOs = medidaPadraoService.list();
            if(!medidasPadraoDTOs.isEmpty())
                return new ResponseEntity<>(medidasPadraoDTOs, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
