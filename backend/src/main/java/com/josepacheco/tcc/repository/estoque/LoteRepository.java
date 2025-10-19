package com.josepacheco.tcc.repository.estoque;

import com.josepacheco.tcc.model.estoque.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteRepository extends JpaRepository<Lote, Long> {
}
