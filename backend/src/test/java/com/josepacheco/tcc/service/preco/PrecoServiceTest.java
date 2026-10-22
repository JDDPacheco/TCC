package com.josepacheco.tcc.service.preco;

import com.josepacheco.tcc.dto.preco.PrecoInputDTO;
import com.josepacheco.tcc.dto.preco.PrecoOutputDTO;
import com.josepacheco.tcc.model.preco.Preco;
import com.josepacheco.tcc.model.produto.Produto;
import com.josepacheco.tcc.repository.preco.PrecoRepository;
import com.josepacheco.tcc.repository.produto.MedidaPadraoRepository;
import com.josepacheco.tcc.repository.produto.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Regras do histórico de preços: cada alteração cria um registro novo e o preço atual é o mais recente.
 */
@SpringBootTest
@Transactional
class PrecoServiceTest {

    private static final String EAN = "7890000000201";

    @Autowired
    private PrecoService precoService;
    @Autowired
    private PrecoRepository precoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private MedidaPadraoRepository medidaPadraoRepository;

    @BeforeEach
    void cadastrarProduto() {
        produtoRepository.save(new Produto("Escova Dental", EAN, medidaPadraoRepository.findBySigla("UN")));
    }

    @Test
    void registraPrecoComVigenciaImediata() {
        LocalDateTime antes = LocalDateTime.now();

        PrecoOutputDTO preco = precoService.create(preco(EAN, "9.90"));

        assertThat(preco.getId()).isNotNull();
        assertThat(preco.getEan()).isEqualTo(EAN);
        assertThat(preco.getNomeProduto()).isEqualTo("Escova Dental");
        assertThat(preco.getValorVenda()).isEqualByComparingTo("9.90");
        assertThat(preco.getDataInicioVigencia()).isAfterOrEqualTo(antes).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void precoAtualEOMaisRecenteEOHistoricoPreservaOsAnteriores() {
        Long primeiro = precoService.create(preco(EAN, "9.90")).getId();
        voltarVigencia(primeiro, 2);
        Long segundo = precoService.create(preco(EAN, "11.50")).getId();
        voltarVigencia(segundo, 1);
        precoService.create(preco(EAN, "10.75"));

        assertThat(precoService.getPrecoAtual(EAN).getValorVenda()).isEqualByComparingTo("10.75");
        assertThat(precoService.getHistorico(EAN)).extracting(p -> p.getValorVenda().toPlainString())
                .containsExactly("10.75", "11.50", "9.90");
    }

    @Test
    void produtoSemPrecoTemHistoricoVazioEPrecoAtual404() {
        assertThat(precoService.getHistorico(EAN)).isEmpty();
        assertErro(() -> precoService.getPrecoAtual(EAN), HttpStatus.NOT_FOUND, "Nenhum preço encontrado");
    }

    @Test
    void produtoInexistenteRetorna404EmTodasAsOperacoes() {
        assertErro(() -> precoService.create(preco("0000000000000", "5.00")), HttpStatus.NOT_FOUND, "Produto não encontrado");
        assertErro(() -> precoService.getHistorico("0000000000000"), HttpStatus.NOT_FOUND, "Produto não encontrado");
        assertErro(() -> precoService.getPrecoAtual("0000000000000"), HttpStatus.NOT_FOUND, "Nenhum preço encontrado");
    }

    @Test
    void recusaValorZeroOuNegativo() {
        assertErro(() -> precoService.create(preco(EAN, "0")), HttpStatus.BAD_REQUEST, "maior que zero");
        assertErro(() -> precoService.create(preco(EAN, "-1.00")), HttpStatus.BAD_REQUEST, "maior que zero");
        assertThat(precoService.getHistorico(EAN)).isEmpty();
    }

    // ---------------------------------------------------------- auxiliares

    private PrecoInputDTO preco(String ean, String valor) {
        PrecoInputDTO dto = new PrecoInputDTO();
        dto.setEan(ean);
        dto.setValorVenda(new BigDecimal(valor));
        return dto;
    }

    // Registros criados no mesmo instante teriam a mesma data; para o teste de ordem, os anteriores são recuados no tempo
    private void voltarVigencia(Long idPreco, int dias) {
        Preco preco = precoRepository.findById(idPreco).orElseThrow();
        preco.setDataInicioVigencia(preco.getDataInicioVigencia().minusDays(dias));
        precoRepository.saveAndFlush(preco);
    }

    private void assertErro(org.assertj.core.api.ThrowableAssert.ThrowingCallable chamada, HttpStatus status, String trechoDaMensagem) {
        assertThatThrownBy(chamada)
                .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(status);
                    assertThat(e.getReason()).contains(trechoDaMensagem);
                });
    }
}
