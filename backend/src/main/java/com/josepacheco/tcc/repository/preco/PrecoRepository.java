package com.josepacheco.tcc.repository.preco;

import com.josepacheco.tcc.model.preco.Preco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrecoRepository extends JpaRepository<Preco, Long> {
}
