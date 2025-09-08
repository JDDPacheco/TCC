package com.josepacheco.tcc.repository.produto.remedio.atributos;

import com.josepacheco.tcc.model.produto.remedio.atributos.Apresentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApresentacaoRepository extends JpaRepository<Apresentacao, Long> {
    //Apresentacao findByApresentacao(String apresentacao);
}
