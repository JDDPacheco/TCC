package com.josepacheco.tcc.controller.estoque;

import com.josepacheco.tcc.dto.estoque.AjusteEstoqueInputDTO;
import com.josepacheco.tcc.dto.estoque.EntradaEstoqueInputDTO;
import com.josepacheco.tcc.dto.estoque.EstoqueMinimoInputDTO;
import com.josepacheco.tcc.dto.estoque.LoteOutputDTO;
import com.josepacheco.tcc.dto.estoque.MovimentacaoEstoqueInputDTO;
import com.josepacheco.tcc.dto.estoque.MovimentacaoEstoqueOutputDTO;
import com.josepacheco.tcc.dto.estoque.SaldoProdutoOutputDTO;
import com.josepacheco.tcc.service.estoque.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Os erros de regra de negócio saem do service como ResponseStatusException (400, 404, 409)
 * e chegam ao cliente com a mensagem no campo "message".
 */
@RestController
@RequestMapping("/api/estoque")
@Tag(name = "Estoque", description = "Entradas, ajustes, perdas, devoluções, saldos por lote e alertas de vencimento e estoque baixo.")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @Operation(summary = "Saldo de estoque de todos os produtos, com os lotes de cada um.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "204", description = "Não há produtos cadastrados")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SaldoProdutoOutputDTO>> listarSaldos() {
        return listaOuVazio(estoqueService.listarSaldos());
    }

    @Operation(summary = "Saldo de estoque de um produto, com os lotes do que vence primeiro para o último.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping(value = "/{ean}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaldoProdutoOutputDTO> consultarSaldo(@PathVariable String ean) {
        return ResponseEntity.ok(estoqueService.consultarSaldo(ean));
    }

    @Operation(summary = "Define (ou remove, enviando null) o estoque mínimo de um produto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estoque mínimo atualizado"),
            @ApiResponse(responseCode = "400", description = "Valor negativo"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping(value = "/{ean}/estoque_minimo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaldoProdutoOutputDTO> definirEstoqueMinimo(@PathVariable String ean, @Valid @RequestBody EstoqueMinimoInputDTO estoqueMinimoDTO) {
        return ResponseEntity.ok(estoqueService.definirEstoqueMinimo(ean, estoqueMinimoDTO.getEstoqueMinimo()));
    }

    @Operation(summary = "Registra a entrada de mercadoria. Remédios exigem código do lote e validade; produtos gerais usam um lote único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrada registrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, lote vencido ou lote existente com outra validade"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PostMapping(value = "/entrada", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovimentacaoEstoqueOutputDTO> registrarEntrada(@Valid @RequestBody EntradaEstoqueInputDTO entradaDTO) {
        return new ResponseEntity<>(estoqueService.registrarEntrada(entradaDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Ajuste de inventário: informa a quantidade contada e o sistema registra a diferença.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ajuste registrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou quantidade igual ao saldo"),
            @ApiResponse(responseCode = "404", description = "Lote não encontrado")
    })
    @PostMapping(value = "/ajuste", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovimentacaoEstoqueOutputDTO> registrarAjuste(@Valid @RequestBody AjusteEstoqueInputDTO ajusteDTO) {
        return new ResponseEntity<>(estoqueService.registrarAjuste(ajusteDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Registra uma perda (avaria, vencimento, extravio), retirando a quantidade do lote.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Perda registrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Lote não encontrado"),
            @ApiResponse(responseCode = "409", description = "Quantidade maior que o saldo do lote")
    })
    @PostMapping(value = "/perda", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovimentacaoEstoqueOutputDTO> registrarPerda(@Valid @RequestBody MovimentacaoEstoqueInputDTO perdaDTO) {
        return new ResponseEntity<>(estoqueService.registrarPerda(perdaDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Registra a devolução de um cliente, devolvendo a quantidade ao lote.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Devolução registrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Lote não encontrado")
    })
    @PostMapping(value = "/devolucao", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovimentacaoEstoqueOutputDTO> registrarDevolucao(@Valid @RequestBody MovimentacaoEstoqueInputDTO devolucaoDTO) {
        return new ResponseEntity<>(estoqueService.registrarDevolucao(devolucaoDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Lotes com saldo que vencem nos próximos N dias (padrão: 30).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Há lotes a vencer"),
            @ApiResponse(responseCode = "204", description = "Nenhum lote a vencer no período"),
            @ApiResponse(responseCode = "400", description = "Número de dias negativo")
    })
    @GetMapping(value = "/alertas/vencimento", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LoteOutputDTO>> listarLotesAVencer(
            @Parameter(description = "janela de dias a partir de hoje", example = "30")
            @RequestParam(defaultValue = "" + EstoqueService.DIAS_ALERTA_VENCIMENTO) int dias) {
        return listaOuVazio(estoqueService.listarLotesAVencer(dias));
    }

    @Operation(summary = "Lotes vencidos que ainda têm saldo (bloqueados para venda, aguardando baixa por perda).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Há lotes vencidos com saldo"),
            @ApiResponse(responseCode = "204", description = "Nenhum lote vencido com saldo")
    })
    @GetMapping(value = "/alertas/vencidos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LoteOutputDTO>> listarLotesVencidos() {
        return listaOuVazio(estoqueService.listarLotesVencidos());
    }

    @Operation(summary = "Produtos com saldo disponível igual ou abaixo do estoque mínimo configurado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Há produtos com estoque baixo"),
            @ApiResponse(responseCode = "204", description = "Nenhum produto com estoque baixo")
    })
    @GetMapping(value = "/alertas/estoque_baixo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SaldoProdutoOutputDTO>> listarEstoqueBaixo() {
        return listaOuVazio(estoqueService.listarEstoqueBaixo());
    }

    @Operation(summary = "Histórico de movimentações (mais recentes primeiro), filtrado por lote, por produto ou completo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Há movimentações"),
            @ApiResponse(responseCode = "204", description = "Nenhuma movimentação encontrada"),
            @ApiResponse(responseCode = "404", description = "Produto ou lote não encontrado")
    })
    @GetMapping(value = "/movimentacoes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MovimentacaoEstoqueOutputDTO>> listarMovimentacoes(
            @Parameter(description = "EAN do produto (opcional)") @RequestParam(required = false) String ean,
            @Parameter(description = "id do lote (opcional; tem prioridade sobre o EAN)") @RequestParam(required = false) Long idLote) {
        return listaOuVazio(estoqueService.listarMovimentacoes(ean, idLote));
    }

    private <T> ResponseEntity<List<T>> listaOuVazio(List<T> lista) {
        return lista.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(lista, HttpStatus.OK);
    }
}
