package com.josepacheco.tcc.repository.estoque;

import com.josepacheco.tcc.model.estoque.Lote;
import com.josepacheco.tcc.model.produto.Produto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    Optional<Lote> findByProdutoAndCodigoLote(Produto produto, String codigoLote);

    @Query("SELECT l FROM Lote l WHERE l.produto = :produto ORDER BY l.dataValidade ASC NULLS LAST, l.id ASC")
    List<Lote> findByProdutoOrdenadosPorValidade(@Param("produto") Produto produto);

    // Lotes que podem ser vendidos, na ordem FEFO (o que vence primeiro sai primeiro).
    // Trava as linhas até o fim da transação para que vendas simultâneas não consumam o mesmo saldo.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lote l WHERE l.produto = :produto AND l.quantidadeAtual > 0 " +
            "AND (l.dataValidade IS NULL OR l.dataValidade >= :hoje) " +
            "ORDER BY l.dataValidade ASC NULLS LAST, l.id ASC")
    List<Lote> findDisponiveisParaBaixa(@Param("produto") Produto produto, @Param("hoje") LocalDate hoje);

    @Query("SELECT l FROM Lote l WHERE l.quantidadeAtual > 0 AND l.dataValidade BETWEEN :inicio AND :fim " +
            "ORDER BY l.dataValidade ASC, l.id ASC")
    List<Lote> findComSaldoVencendoEntre(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT l FROM Lote l WHERE l.quantidadeAtual > 0 AND l.dataValidade < :hoje ORDER BY l.dataValidade ASC, l.id ASC")
    List<Lote> findComSaldoVencidos(@Param("hoje") LocalDate hoje);
}
