package com.josepacheco.tcc.controller.preco;

import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Camada HTTP do módulo de preço: validação, status e formato das respostas usadas pela tela de Preços.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PrecoControllerTest {

    private static final String EAN = "7890000000401";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MedidaPadraoRepository medidaPadraoRepository;

    @BeforeEach
    void cadastrarProduto() {
        produtoRepository.save(new Produto("Escova Dental", EAN, medidaPadraoRepository.findBySigla("UN")));
    }

    @Test
    void registraEConsultaPreco() throws Exception {
        mockMvc.perform(post("/api/preco").contentType(MediaType.APPLICATION_JSON).content("{\"ean\": \"" + EAN + "\", \"valorVenda\": 12.90}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ean").value(EAN))
                .andExpect(jsonPath("$.nomeProduto").value("Escova Dental"))
                .andExpect(jsonPath("$.valorVenda").value(12.90))
                .andExpect(jsonPath("$.dataInicioVigencia").isNotEmpty());

        mockMvc.perform(get("/api/preco/" + EAN + "/atual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorVenda").value(12.90));
        mockMvc.perform(get("/api/preco/" + EAN + "/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void cadastroSemCamposObrigatoriosRetorna400ComAsMensagens() throws Exception {
        mockMvc.perform(post("/api/preco").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O EAN do produto é obrigatório.")))
                .andExpect(jsonPath("$.message", containsString("O valor de venda é obrigatório.")));
    }

    @Test
    void valorZeroRetorna400() throws Exception {
        mockMvc.perform(post("/api/preco").contentType(MediaType.APPLICATION_JSON).content("{\"ean\": \"" + EAN + "\", \"valorVenda\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O valor de venda deve ser maior que zero.")));
    }

    // A tela de Preços usa o histórico para saber se o produto existe: vazio (200) = existe sem preço; 404 = não existe
    @Test
    void historicoDeProdutoSemPrecoRetornaListaVaziaEProdutoInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/preco/" + EAN + "/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get("/api/preco/0000000000000/historico"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(containsString("Produto não encontrado")));
    }

    @Test
    void precoAtualDeProdutoSemPrecoRetorna404() throws Exception {
        mockMvc.perform(get("/api/preco/" + EAN + "/atual"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(containsString("Nenhum preço encontrado")));
    }

    @Test
    void precoParaProdutoInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/api/preco").contentType(MediaType.APPLICATION_JSON).content("{\"ean\": \"0000000000000\", \"valorVenda\": 5}"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(containsString("Produto não encontrado")));
    }
}
