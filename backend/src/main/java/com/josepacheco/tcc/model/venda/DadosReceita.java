package com.josepacheco.tcc.model.venda;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Dados da receita retida na venda de remédios de controle especial.
 * Ficam gravados junto do item vendido (colunas em item_venda); não há integração com o SNGPC.
 */
@Embeddable
public class DadosReceita {

    @Column(name = "receita_nome_paciente")
    private String nomePaciente;

    @Column(name = "receita_nome_prescritor")
    private String nomePrescritor;

    @Column(name = "receita_registro_prescritor", length = 30)
    private String registroPrescritor; // ex.: "CRM-AM 12345"

    @Column(name = "receita_numero", length = 50)
    private String numeroReceita;

    public DadosReceita() {}

    public DadosReceita(String nomePaciente, String nomePrescritor, String registroPrescritor, String numeroReceita) {
        this.nomePaciente = nomePaciente;
        this.nomePrescritor = nomePrescritor;
        this.registroPrescritor = registroPrescritor;
        this.numeroReceita = numeroReceita;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public String getNomePrescritor() {
        return nomePrescritor;
    }

    public String getRegistroPrescritor() {
        return registroPrescritor;
    }

    public String getNumeroReceita() {
        return numeroReceita;
    }
}
