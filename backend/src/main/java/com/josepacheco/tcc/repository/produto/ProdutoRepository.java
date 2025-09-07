package com.josepacheco.tcc.repository.produto;

import com.josepacheco.tcc.model.produto.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
