package com.josepacheco.tcc.model.venda;

public enum FormaPagamento {
    DINHEIRO,        // exige valor recebido; o sistema calcula o troco
    CARTAO_DEBITO,
    CARTAO_CREDITO,
    PIX
}
