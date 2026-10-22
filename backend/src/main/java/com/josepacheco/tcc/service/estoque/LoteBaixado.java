package com.josepacheco.tcc.service.estoque;

import com.josepacheco.tcc.model.estoque.Lote;

/**
 * Parte de uma baixa de estoque: quanto saiu de cada lote.
 * O módulo de vendas usa o lote para preencher ItemVenda.lote.
 */
public record LoteBaixado(Lote lote, Integer quantidade) {
}
