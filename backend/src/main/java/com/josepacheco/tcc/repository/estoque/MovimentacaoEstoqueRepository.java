package com.josepacheco.tcc.repository.estoque;

import com.josepacheco.tcc.model.estoque.Lote;
import com.josepacheco.tcc.model.estoque.MovimentacaoEstoque;
import com.josepacheco.tcc.model.produto.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByLoteOrderByDataHoraDescIdDesc(Lote lote);

    List<MovimentacaoEstoque> findByLoteProdutoOrderByDataHoraDescIdDesc(Produto produto);

    List<MovimentacaoEstoque> findAllByOrderByDataHoraDescIdDesc();
}
