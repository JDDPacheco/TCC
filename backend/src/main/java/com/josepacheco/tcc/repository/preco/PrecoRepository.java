package com.josepacheco.tcc.repository.preco;

import com.josepacheco.tcc.model.preco.Preco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrecoRepository extends JpaRepository<Preco, Long> {

    Optional<Preco> findTopByProdutoEanOrderByDataInicioVigenciaDesc(String ean);

    List<Preco> findByProdutoEanOrderByDataInicioVigenciaDesc(String ean);

    // Preço atual (o de vigência mais recente) de todos os produtos que têm preço, em uma única consulta
    @Query("SELECT p FROM Preco p WHERE p.dataInicioVigencia = " +
            "(SELECT MAX(p2.dataInicioVigencia) FROM Preco p2 WHERE p2.produto = p.produto)")
    List<Preco> findPrecosAtuais();
}