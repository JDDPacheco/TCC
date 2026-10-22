package com.josepacheco.tcc.controller.produto;

import com.josepacheco.tcc.repository.produto.remedio.atributos.LaboratorioRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.ComposicaoRepository;
import com.josepacheco.tcc.repository.produto.remedio.formulacao.FormulaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Camada HTTP do módulo de produto: JSON polimórfico (tipoProduto), validação dos DTOs, códigos de status,
 * listas de apoio e regressões de bugs já corrigidos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FormulaRepository formulaRepository;
    @Autowired
    private LaboratorioRepository laboratorioRepository;
    @Autowired
    private ComposicaoRepository composicaoRepository;

    private Long idFormula;
    private Long idLaboratorio;

    @BeforeEach
    void carregarDadosDeApoio() {
        idFormula = formulaRepository.findAll().getFirst().getId();
        idLaboratorio = laboratorioRepository.findAll().getFirst().getId();
    }

    // ---------------------------------------------------------- listas de apoio (populadas pelo DataLoader)

    @Test
    void listasDeApoioRetornamOsValoresIniciais() throws Exception {
        mockMvc.perform(get("/api/produto/medida_padrao"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[*].sigla", hasItems("CX", "UN", "VD")));
        mockMvc.perform(get("/api/produto/remedio/apresentacao"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[*].sigla", hasItems("CP", "XA", "GE")));
        mockMvc.perform(get("/api/produto/remedio/receita"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[*].tipo", hasItems("isento", "sob_prescricao", "especial")));
        mockMvc.perform(get("/api/produto/remedio/medida_farmaceutica"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[*].sigla", hasItems("CP", "CD", "FR")));
        mockMvc.perform(get("/api/produto/remedio/formulacao/medida_basica"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[*].sigla", hasItems("g", "mg", "ml")));
    }

    // ---------------------------------------------------------- cadastro de produtos

    @Test
    void cadastraProdutoGeral() throws Exception {
        cadastrar(produtoGeral("7890000000301", "Escova Dental"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoProduto").value("geral"))
                .andExpect(jsonPath("$.unidadeDeMedida").value("UN"));

        mockMvc.perform(get("/api/produto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].ean", hasItems("7890000000301")));
    }

    @Test
    void cadastraRemedioEGenericoPeloTipoProduto() throws Exception {
        cadastrar(remedio("remedio", "7890000000302", "Dipirona 1g"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoProduto").value("remedio"))
                .andExpect(jsonPath("$.formula").value("dipirona monoidratada 1 g"))
                .andExpect(jsonPath("$.nomeLaboratorio").value("Prati-Donaduzzi"))
                .andExpect(jsonPath("$.apresentacao").value("Comprimido"));

        cadastrar(remedio("generico", "7890000000303", "Dipirona Genérico"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoProduto").value("generico"))
                .andExpect(jsonPath("$.nomeComercial").value("Dipirona Genérico"));
    }

    @Test
    void cadastroSemCamposObrigatoriosRetorna400ComAsMensagens() throws Exception {
        cadastrar("{\"tipoProduto\": \"geral\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O código ean é obrigatório.")))
                .andExpect(jsonPath("$.message", containsString("O nome comercial do produto é obrigatório.")))
                .andExpect(jsonPath("$.message", containsString("A unidade de medida padrão para o produto é obrigatório.")));
    }

    @Test
    void remedioSemFormulaOuLaboratorioRetorna400() throws Exception {
        String corpo = """
                {"tipoProduto": "remedio", "ean": "7890000000304", "nomeComercial": "X", "unidadeDeMedida": "CX",
                 "siglaApresentacao": "CP", "tipoControle": "isento"}
                """;
        cadastrar(corpo)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O id da fórmula é obrigatório.")))
                .andExpect(jsonPath("$.message", containsString("O id do laboratório é obrigatório.")));
    }

    @Test
    void tipoProdutoDesconhecidoRetorna400() throws Exception {
        cadastrar("{\"tipoProduto\": \"cosmetico\", \"ean\": \"1\", \"nomeComercial\": \"X\", \"unidadeDeMedida\": \"UN\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("mal formatado")));
    }

    // ---------------------------------------------------------- atualização de produtos

    @Test
    void atualizaProdutoGeral() throws Exception {
        cadastrar(produtoGeral("7890000000305", "Escova")).andExpect(status().isCreated());

        mockMvc.perform(put("/api/produto/7890000000305").contentType(MediaType.APPLICATION_JSON)
                        .content(produtoGeral("7890000000305", "Escova Macia")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeComercial").value("Escova Macia"));
    }

    // Com a validação ativa, o corpo do PUT precisa trazer o EAN (exigido pelo ProdutoDTO), mesmo ele já estar na URL
    @Test
    void atualizacaoDeRemedioSemEanNoCorpoRetorna400() throws Exception {
        cadastrar(remedio("remedio", "7890000000306", "Dipirona 1g")).andExpect(status().isCreated());
        String corpoSemEan = remedio("remedio", "7890000000306", "Dipirona 1g").replace("\"ean\": \"7890000000306\",", "");

        mockMvc.perform(put("/api/produto/7890000000306").contentType(MediaType.APPLICATION_JSON).content(corpoSemEan))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O código ean é obrigatório.")));
    }

    // ---------------------------------------------------------- laboratórios, princípios ativos, composições e fórmulas

    @Test
    void laboratorioExigeMarcaENomeFantasiaENaoAceitaId() throws Exception {
        mockMvc.perform(post("/api/produto/remedio/laboratorio").contentType(MediaType.APPLICATION_JSON).content("{\"marca\": \"EMS\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O Nome Fantasia é obrigatório.")));

        mockMvc.perform(post("/api/produto/remedio/laboratorio").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 5, \"marca\": \"EMS\", \"nomeFantasia\": \"EMS S/A\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O id deve ser omitido")));

        mockMvc.perform(post("/api/produto/remedio/laboratorio").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\": \"EMS\", \"nomeFantasia\": \"EMS S/A\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void principioAtivoExigeNome() throws Exception {
        mockMvc.perform(post("/api/produto/remedio/formulacao/principio_ativo").contentType(MediaType.APPLICATION_JSON).content("{\"nome\": \" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O nome do princípio ativo é obrigatório.")));

        mockMvc.perform(post("/api/produto/remedio/formulacao/principio_ativo").contentType(MediaType.APPLICATION_JSON).content("{\"nome\": \"nimesulida\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("nimesulida"));
    }

    @Test
    void cadastraComposicaoEFormula() throws Exception {
        mockMvc.perform(post("/api/produto/remedio/formulacao/composicao").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"principioAtivo\": \"dipirona monoidratada\", \"quantiaPrincipio\": 500, \"unidadeMedidaPrincipio\": \"mg\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.unidadeMedidaPrincipio").value("mg"));

        Long idComposicao = composicaoRepository.findAll().getLast().getId();
        mockMvc.perform(post("/api/produto/remedio/formulacao/formula").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idComposicoes\": [" + idComposicao + "]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.composicoes[0]").value("dipirona monoidratada 500 mg"));
    }

    @Test
    void formulaSemComposicoesRetorna400() throws Exception {
        mockMvc.perform(post("/api/produto/remedio/formulacao/formula").contentType(MediaType.APPLICATION_JSON).content("{\"idComposicoes\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("A lista de ids das composições é obrigatória.")));
    }

    // ---------------------------------------------------------- regressões de bugs corrigidos

    // Antes, o update recebia o corpo como texto puro (@RequestBody String) e salvava o próprio JSON como nome
    @Test
    void corrigirPrincipioAtivoComOCorpoEnviadoPelaTelaSalvaApenasONome() throws Exception {
        mockMvc.perform(post("/api/produto/remedio/formulacao/principio_ativo").contentType(MediaType.APPLICATION_JSON).content("{\"nome\": \"nimesulda\"}"))
                .andExpect(status().isCreated());
        Long id = Long.valueOf(com.jayway.jsonpath.JsonPath.read(
                mockMvc.perform(get("/api/produto/remedio/formulacao/principio_ativo")).andReturn().getResponse().getContentAsString(),
                "$[-1].id").toString());

        mockMvc.perform(put("/api/produto/remedio/formulacao/principio_ativo/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"nimesulida\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("nimesulida"));

        mockMvc.perform(put("/api/produto/remedio/formulacao/principio_ativo/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("O nome do princípio ativo é obrigatório.")));
    }

    // Antes, os services usavam getReferenceById (que não falha para id inexistente) e o catch genérico respondia 500.
    // Obs.: no MockMvc a mensagem de uma ResponseStatusException é conferida no "reason" (o corpo JSON vem da página /error)
    @Test
    void detalhesEAtualizacaoDeIdInexistenteRetornam404() throws Exception {
        mockMvc.perform(get("/api/produto/remedio/laboratorio/999999"))
                .andExpect(status().isNotFound()).andExpect(status().reason(containsString("Laboratório não encontrado")));
        mockMvc.perform(get("/api/produto/remedio/formulacao/principio_ativo/999999"))
                .andExpect(status().isNotFound()).andExpect(status().reason(containsString("Princípio ativo não encontrado")));
        mockMvc.perform(get("/api/produto/remedio/formulacao/composicao/999999"))
                .andExpect(status().isNotFound()).andExpect(status().reason(containsString("Composição não encontrada")));
        mockMvc.perform(get("/api/produto/remedio/formulacao/formula/999999"))
                .andExpect(status().isNotFound()).andExpect(status().reason(containsString("Fórmula não encontrada")));
        mockMvc.perform(put("/api/produto/remedio/laboratorio/999999").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\": \"EMS\", \"nomeFantasia\": \"EMS S/A\"}"))
                .andExpect(status().isNotFound());
    }

    // Antes, o catch genérico do ProdutoController.update respondia 500 e escondia o 404 do ProdutoService
    @Test
    void atualizarProdutoInexistenteRetorna404() throws Exception {
        mockMvc.perform(put("/api/produto/0000000000000").contentType(MediaType.APPLICATION_JSON).content(produtoGeral("0000000000000", "X")))
                .andExpect(status().isNotFound())
                .andExpect(status().reason(containsString("Produto não encontrado")));
    }

    // Antes, o catch genérico do ProdutoController.update respondia 500 e escondia o 400 (troca de tipo) do ProdutoService
    @Test
    void atualizarTrocandoOTipoDoProdutoRetorna400() throws Exception {
        cadastrar(produtoGeral("7890000000307", "Escova")).andExpect(status().isCreated());

        mockMvc.perform(put("/api/produto/7890000000307").contentType(MediaType.APPLICATION_JSON).content(remedio("remedio", "7890000000307", "Escova")))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("tipo fundamental")));
    }

    // Antes, o ProdutoController.create convertia o 409 do ProdutoService em 400 com a mensagem "409 CONFLICT ..."
    @Test
    void cadastrarEanDuplicadoRetorna409() throws Exception {
        cadastrar(produtoGeral("7890000000308", "Produto A")).andExpect(status().isCreated());
        cadastrar(produtoGeral("7890000000308", "Produto B"))
                .andExpect(status().isConflict())
                .andExpect(status().reason(containsString("Já existe um produto com o EAN")));
    }

    // ---------------------------------------------------------- auxiliares

    private ResultActions cadastrar(String corpo) throws Exception {
        return mockMvc.perform(post("/api/produto").contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    private String produtoGeral(String ean, String nome) {
        return """
                {"tipoProduto": "geral", "ean": "%s", "nomeComercial": "%s", "unidadeDeMedida": "UN"}
                """.formatted(ean, nome);
    }

    private String remedio(String tipo, String ean, String nome) {
        return """
                {"tipoProduto": "%s", "ean": "%s", "nomeComercial": "%s", "unidadeDeMedida": "CX",
                 "idFormula": %d, "idLaboratorio": %d, "quantidadeDoses": 10, "siglaMedidaDoses": "CP",
                 "siglaApresentacao": "CP", "tipoControle": "isento"}
                """.formatted(tipo, ean, nome, idFormula, idLaboratorio);
    }
}
