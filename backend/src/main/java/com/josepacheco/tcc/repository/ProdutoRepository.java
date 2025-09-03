package com.josepacheco.tcc.repository;

import com.josepacheco.tcc.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
