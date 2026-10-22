package com.josepacheco.tcc.model.venda;

public enum StatusVenda {
    CONCLUIDA,  // venda finalizada no caixa
    CANCELADA   // venda desfeita; os itens voltaram aos lotes (a venda não é apagada)
}
