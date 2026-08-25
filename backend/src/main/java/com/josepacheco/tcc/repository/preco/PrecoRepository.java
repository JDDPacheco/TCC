package com.josepacheco.tcc.repository.preco;

import com.josepacheco.tcc.model.preco.Preco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrecoRepository extends JpaRepository<Preco, Long> {

    Optional<Preco> findTopByProdutoEanOrderByDataInicioVigenciaDesc(String ean);

    List<Preco> findByProdutoEanOrderByDataInicioVigenciaDesc(String ean);
}