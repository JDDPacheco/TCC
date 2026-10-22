package com.josepacheco.tcc.controller.venda;

import com.josepacheco.tcc.dto.venda.CancelamentoVendaInputDTO;
import com.josepacheco.tcc.dto.venda.ProdutoPdvOutputDTO;
import com.josepacheco.tcc.dto.venda.ResumoVendasOutputDTO;
import com.josepacheco.tcc.dto.venda.VendaInputDTO;
import com.josepacheco.tcc.dto.venda.VendaOutputDTO;
import com.josepacheco.tcc.service.venda.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Os erros de regra de negócio saem do service como ResponseStatusException (400, 404, 409)
 * e chegam ao cliente com a mensagem no campo "message".
 */
@RestController
@RequestMapping("/api/venda")
@Tag(name = "Vendas", description = "Caixa (PDV): registro, cancelamento e consulta de vendas, e resumo do dia.")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @Operation(summary = "Consulta um produto para o caixa: preço atual, saldo disponível e exigência de receita.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping(value = "/produto/{ean}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProdutoPdvOutputDTO> consultarProduto(@PathVariable String ean) {
        return ResponseEntity.ok(vendaService.consultarProdutoParaVenda(ean));
    }

    @Operation(summary = "Registra uma venda: usa o preço atual de cada produto, dá baixa no estoque por FEFO e calcula desconto e troco.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venda registrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, receita não confirmada, desconto maior que o total ou valor recebido insuficiente"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Produto sem preço cadastrado ou estoque insuficiente")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VendaOutputDTO> registrarVenda(@Valid @RequestBody VendaInputDTO vendaDTO) {
        return new ResponseEntity<>(vendaService.registrarVenda(vendaDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Lista as vendas (concluídas e canceladas) de um período, da mais recente para a mais antiga. Sem datas, lista as de hoje.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Há vendas no período"),
            @ApiResponse(responseCode = "204", description = "Nenhuma venda no período"),
            @ApiResponse(responseCode = "400", description = "Data final anterior à inicial ou data mal formatada")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VendaOutputDTO>> listarVendas(
            @Parameter(description = "data inicial (aaaa-mm-dd)", example = "2026-10-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "data final, inclusive (aaaa-mm-dd)", example = "2026-10-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        List<VendaOutputDTO> vendas = vendaService.listarVendas(inicio, fim);
        return vendas.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : ResponseEntity.ok(vendas);
    }

    @Operation(summary = "Resumo (fechamento de caixa) de um dia: totais, descontos, ticket médio e totais por forma de pagamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumo calculado (mesmo sem vendas)"),
            @ApiResponse(responseCode = "400", description = "Data mal formatada")
    })
    @GetMapping(value = "/resumo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResumoVendasOutputDTO> resumoDoDia(
            @Parameter(description = "dia (aaaa-mm-dd); padrão: hoje", example = "2026-10-22")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(vendaService.resumoDoDia(data));
    }

    @Operation(summary = "Detalhes de uma venda pelo número.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda encontrada"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VendaOutputDTO> consultarVenda(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.consultarVenda(id));
    }

    @Operation(summary = "Cancela uma venda concluída: a venda é marcada como cancelada e os itens voltam aos lotes de origem.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda cancelada"),
            @ApiResponse(responseCode = "400", description = "Motivo não informado"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada"),
            @ApiResponse(responseCode = "409", description = "Venda já cancelada")
    })
    @PostMapping(value = "/{id}/cancelamento", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VendaOutputDTO> cancelarVenda(@PathVariable Long id, @Valid @RequestBody CancelamentoVendaInputDTO cancelamentoDTO) {
        return ResponseEntity.ok(vendaService.cancelarVenda(id, cancelamentoDTO.getMotivo()));
    }
}
