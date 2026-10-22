package com.josepacheco.tcc.controller.estoque;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes da camada HTTP: validação dos DTOs (@Valid), códigos de status e mensagens de erro no corpo.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EstoqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void entradaSemCamposObrigatoriosRetorna400ComAsMensagens() throws Exception {
        mockMvc.perform(post("/api/estoque/entrada").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O EAN do produto é obrigatório.")))
                .andExpect(jsonPath("$.message", containsString("A quantidade é obrigatória.")))
                .andExpect(jsonPath("$.message", containsString("O fornecedor é obrigatório.")));
    }

    @Test
    void entradaComQuantidadeNegativaRetorna400() throws Exception {
        String corpo = """
                {"ean": "7890000000001", "quantidade": -2, "custoUnitario": 5, "fornecedor": "Fornecedor"}
                """;
        mockMvc.perform(post("/api/estoque/entrada").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("A quantidade deve ser maior que zero.")));
    }

    @Test
    void entradaComDataMalFormatadaRetorna400() throws Exception {
        String corpo = """
                {"ean": "7890000000001", "dataValidade": "31/12/2027", "quantidade": 1, "custoUnitario": 5, "fornecedor": "Fornecedor"}
                """;
        mockMvc.perform(post("/api/estoque/entrada").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("mal formatado")));
    }

    // No servidor real, o corpo JSON das ResponseStatusException é montado pela página /error do Spring Boot,
    // que o MockMvc não executa; aqui a mensagem é conferida no "reason" da resposta
    @Test
    void entradaDeProdutoInexistenteRetorna404ComMensagem() throws Exception {
        String corpo = """
                {"ean": "0000000000000", "quantidade": 1, "custoUnitario": 5, "fornecedor": "Fornecedor"}
                """;
        mockMvc.perform(post("/api/estoque/entrada").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(containsString("Produto não encontrado")));
    }

    @Test
    void perdaSemObservacaoRetorna400() throws Exception {
        mockMvc.perform(post("/api/estoque/perda").contentType(MediaType.APPLICATION_JSON).content("{\"idLote\": 1, \"quantidade\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("A observação (motivo) é obrigatória.")));
    }

    @Test
    void alertasSemLotesRetornam204() throws Exception {
        mockMvc.perform(get("/api/estoque/alertas/vencidos")).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/estoque/alertas/vencimento")).andExpect(status().isNoContent());
    }

    // Garante que ativar o spring-boot-starter-validation não quebrou o cadastro de composição
    // (antes havia um @NotBlank num campo float, o que gera erro 500 quando a validação está ativa)
    @Test
    void composicaoComQuantiaZeroRetorna400EmVezDe500() throws Exception {
        String corpo = """
                {"principioAtivo": "dipirona monoidratada", "quantiaPrincipio": 0, "unidadeMedidaPrincipio": "mg"}
                """;
        mockMvc.perform(post("/api/produto/remedio/formulacao/composicao").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("maior que zero")));
    }
}
