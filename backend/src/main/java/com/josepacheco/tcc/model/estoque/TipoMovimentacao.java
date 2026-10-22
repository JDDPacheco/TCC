package com.josepacheco.tcc.model.estoque;

public enum TipoMovimentacao {
    ENTRADA,            // entrada de mercadoria comprada do fornecedor
    SAIDA_VENDA,        // baixa automática feita pelo módulo de vendas (FEFO)
    AJUSTE_INVENTARIO,  // correção do saldo após contagem física (pode somar ou subtrair)
    PERDA,              // avaria, vencimento, extravio
    DEVOLUCAO           // produto devolvido por cliente e reintegrado ao lote
}
