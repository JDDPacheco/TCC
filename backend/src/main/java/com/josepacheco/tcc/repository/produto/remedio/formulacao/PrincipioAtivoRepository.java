package com.josepacheco.tcc.repository.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.PrincipioAtivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrincipioAtivoRepository extends JpaRepository<PrincipioAtivo, Long> {
    @Query("select p from PrincipioAtivo p where p.nome = :parNome")
    PrincipioAtivo findByNome(@Param("parNome") String nome);
}
