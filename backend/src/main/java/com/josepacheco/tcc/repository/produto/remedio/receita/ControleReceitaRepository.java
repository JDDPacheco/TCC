package com.josepacheco.tcc.repository.produto.remedio.receita;

import com.josepacheco.tcc.model.produto.remedio.receita.ControleReceita;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ControleReceitaRepository extends JpaRepository<ControleReceita, Long> {
}
