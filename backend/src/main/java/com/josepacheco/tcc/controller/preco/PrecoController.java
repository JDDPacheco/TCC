package com.josepacheco.tcc.controller.preco;

import com.josepacheco.tcc.dto.preco.PrecoInputDTO;
import com.josepacheco.tcc.dto.preco.PrecoOutputDTO;
import com.josepacheco.tcc.service.preco.PrecoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/preco")
public class PrecoController {

    @Autowired
    private PrecoService precoService;

    @PostMapping
    public ResponseEntity<PrecoOutputDTO> create(
            @Valid @RequestBody PrecoInputDTO precoInputDTO) {

        PrecoOutputDTO precoCriado = precoService.create(precoInputDTO);

        return new ResponseEntity<>(precoCriado, HttpStatus.CREATED);
    }

    @GetMapping("/{ean}/atual")
    public ResponseEntity<PrecoOutputDTO> getPrecoAtual(
            @PathVariable String ean) {

        return ResponseEntity.ok(precoService.getPrecoAtual(ean));
    }

    @GetMapping("/{ean}/historico")
    public ResponseEntity<List<PrecoOutputDTO>> getHistorico(
            @PathVariable String ean) {

        return ResponseEntity.ok(precoService.getHistorico(ean));
    }
}