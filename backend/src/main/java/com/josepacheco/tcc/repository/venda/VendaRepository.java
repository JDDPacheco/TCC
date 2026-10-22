package com.josepacheco.tcc.repository.venda;

import com.josepacheco.tcc.model.venda.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Vendas com dataVenda em [inicio, fim), da mais recente para a mais antiga
    List<Venda> findByDataVendaGreaterThanEqualAndDataVendaLessThanOrderByDataVendaDescIdDesc(LocalDateTime inicio, LocalDateTime fim);
}
