package com.josepacheco.tcc.repository.estoque;

import com.josepacheco.tcc.model.estoque.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
}
