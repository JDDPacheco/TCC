package com.josepacheco.tcc.controller.venda;

import com.jayway.jsonpath.JsonPath;
import com.josepacheco.tcc.dto.estoque.EntradaEstoqueInputDTO;
import com.josepacheco.tcc.dto.preco.PrecoInputDTO;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import com.josepacheco.tcc.service.estoque.EstoqueService;
import com.josepacheco.tcc.service.preco.PrecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Camada HTTP do módulo de vendas: validação (inclusive dos itens aninhados), status e formato das respostas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VendaControllerTest {

    private static final String EAN = "7890000000601"; // R$ 5,00 · 10 UN

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MedidaPadraoRepository medidaPadraoRepository;
    @Autowired
    private PrecoService precoService;
    @Autowired
    private EstoqueService estoqueService;

    @BeforeEach
    void prepararProduto() {
        produtoRepository.save(new Produto("Escova Dental", EAN, medidaPadraoRepository.findBySigla("UN")));

        PrecoInputDTO preco = new PrecoInputDTO();
        preco.setEan(EAN);
        preco.setValorVenda(new BigDecimal("5.00"));
        precoService.create(preco);

        EntradaEstoqueInputDTO entrada = new EntradaEstoqueInputDTO();
        entrada.setEan(EAN);
        entrada.setQuantidade(10);
        entrada.setCustoUnitario(new BigDecimal("2.00"));
        entrada.setFornecedor("Distribuidora Teste");
        estoqueService.registrarEntrada(entrada);
    }

    @Test
    void registraVendaEmDinheiroComTroco() throws Exception {
        registrar("""
                {"itens": [{"ean": "%s", "quantidade": 3}], "formaPagamento": "DINHEIRO", "desconto": 1, "valorRecebido": 20}
                """.formatted(EAN))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.itens", hasSize(1)))
                .andExpect(jsonPath("$.itens[0].codigoLote").value("UNICO"))
                .andExpect(jsonPath("$.itens[0].subtotal").value(15.00))
                .andExpect(jsonPath("$.valorTotal").value(14.00))
                .andExpect(jsonPath("$.troco").value(6.00));
    }

    @Test
    void vendaSemItensNemPagamentoRetorna400ComAsMensagens() throws Exception {
        registrar("{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("A venda deve ter pelo menos um item.")))
                .andExpect(jsonPath("$.message", containsString("A forma de pagamento é obrigatória.")));
    }

    @Test
    void itensSaoValidadosIndividualmente() throws Exception {
        registrar("{\"itens\": [{\"ean\": \"\", \"quantidade\": 0}], \"formaPagamento\": \"PIX\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O EAN de cada item é obrigatório.")))
                .andExpect(jsonPath("$.message", containsString("A quantidade de cada item deve ser maior que zero.")));
    }

    @Test
    void formaDePagamentoDesconhecidaRetorna400() throws Exception {
        registrar("{\"itens\": [{\"ean\": \"%s\", \"quantidade\": 1}], \"formaPagamento\": \"CHEQUE\"}".formatted(EAN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("mal formatado")));
    }

    @Test
    void estoqueInsuficienteRetorna409() throws Exception {
        registrar("{\"itens\": [{\"ean\": \"%s\", \"quantidade\": 11}], \"formaPagamento\": \"PIX\"}".formatted(EAN))
                .andExpect(status().isConflict())
                .andExpect(status().reason(containsString("Estoque insuficiente")));
    }

    @Test
    void consultaDeProdutoParaOCaixa() throws Exception {
        mockMvc.perform(get("/api/venda/produto/" + EAN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precoAtual").value(5.00))
                .andExpect(jsonPath("$.saldoDisponivel").value(10))
                .andExpect(jsonPath("$.exigeReceita").value(false));
        mockMvc.perform(get("/api/venda/produto/0000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(containsString("Produto não encontrado")));
    }

    @Test
    void cancelamentoExigeMotivoENaoPodeSerRepetido() throws Exception {
        String resposta = registrar("{\"itens\": [{\"ean\": \"%s\", \"quantidade\": 2}], \"formaPagamento\": \"PIX\"}".formatted(EAN))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Integer idVenda = JsonPath.read(resposta, "$.id");

        cancelar(idVenda, "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O motivo do cancelamento é obrigatório.")));
        cancelar(idVenda, "{\"motivo\": \"Cliente desistiu\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"))
                .andExpect(jsonPath("$.motivoCancelamento").value("Cliente desistiu"));
        cancelar(idVenda, "{\"motivo\": \"De novo\"}")
                .andExpect(status().isConflict())
                .andExpect(status().reason(containsString("já foi cancelada")));

        mockMvc.perform(get("/api/venda/" + idVenda)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    @Test
    void consultasDeVendas() throws Exception {
        mockMvc.perform(get("/api/venda/999999"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(containsString("Venda não encontrada")));
        mockMvc.perform(get("/api/venda").param("inicio", "2000-01-01").param("fim", "2000-01-31"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/venda").param("inicio", "31/12/2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O parâmetro 'inicio' é inválido")));
        mockMvc.perform(get("/api/venda/resumo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porFormaPagamento", hasSize(4)));
    }

    // ---------------------------------------------------------- auxiliares

    private ResultActions registrar(String corpo) throws Exception {
        return mockMvc.perform(post("/api/venda").contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    private ResultActions cancelar(Integer idVenda, String corpo) throws Exception {
        return mockMvc.perform(post("/api/venda/" + idVenda + "/cancelamento").contentType(MediaType.APPLICATION_JSON).content(corpo));
    }
}
