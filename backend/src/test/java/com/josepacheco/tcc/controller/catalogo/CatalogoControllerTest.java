package com.josepacheco.tcc.controller.catalogo;

import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CatalogoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MedidaPadraoRepository medidaPadraoRepository;

    @Test
    void semProdutosRetorna204EComProdutosRetornaALista() throws Exception {
        mockMvc.perform(get("/api/catalogo")).andExpect(status().isNoContent());

        produtoRepository.save(new Produto("Escova Dental", "7890000000801", medidaPadraoRepository.findBySigla("UN")));

        mockMvc.perform(get("/api/catalogo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nomeComercial").value("Escova Dental"))
                .andExpect(jsonPath("$[0].saldoDisponivel").value(0))
                .andExpect(jsonPath("$[0].precoAtual").doesNotExist());
    }
}
