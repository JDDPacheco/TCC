package com.josepacheco.tcc.repository.produto.remedio.formulacao;

import com.josepacheco.tcc.model.produto.remedio.formulacao.MedidaBasica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedidaBasicaRepository extends JpaRepository<MedidaBasica, Long> {
    @Query("select m from MedidaBasica m where m.sigla = :parSigla")
    MedidaBasica findBySigla(@Param("parSigla") String sigla);
}
