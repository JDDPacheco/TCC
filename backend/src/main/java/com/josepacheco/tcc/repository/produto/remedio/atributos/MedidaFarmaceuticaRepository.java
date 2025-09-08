package com.josepacheco.tcc.repository.produto.remedio.atributos;

import com.josepacheco.tcc.model.produto.remedio.atributos.MedidaFarmaceutica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedidaFarmaceuticaRepository extends JpaRepository<MedidaFarmaceutica, Long> {
    MedidaFarmaceutica findBySigla(String sigla);
}
