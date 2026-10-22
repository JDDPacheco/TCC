package com.josepacheco.tcc.dto.venda;

import com.josepacheco.tcc.model.venda.DadosReceita;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Dados da receita de um remédio de controle especial. A obrigatoriedade é verificada no service,
 * porque só se aplica a itens de controle especial.
 */
public class DadosReceitaDTO {

    @Size(max = 255, message = "O nome do paciente deve ter no máximo 255 caracteres.")
    @Schema(description = "nome do paciente", example = "Maria da Silva")
    private String nomePaciente;

    @Size(max = 255, message = "O nome do prescritor deve ter no máximo 255 caracteres.")
    @Schema(description = "nome do médico/dentista que prescreveu", example = "Dr. João Souza")
    private String nomePrescritor;

    @Size(max = 30, message = "O registro do prescritor deve ter no máximo 30 caracteres.")
    @Schema(description = "conselho, UF e número do registro do prescritor", example = "CRM-AM 12345")
    private String registroPrescritor;

    @Size(max = 50, message = "O número da receita deve ter no máximo 50 caracteres.")
    @Schema(description = "número da receita/notificação", example = "AM-0012345")
    private String numeroReceita;

    public DadosReceitaDTO() {
    }

    public DadosReceitaDTO(DadosReceita receita) {
        this.nomePaciente = receita.getNomePaciente();
        this.nomePrescritor = receita.getNomePrescritor();
        this.registroPrescritor = receita.getRegistroPrescritor();
        this.numeroReceita = receita.getNumeroReceita();
    }

    public boolean isCompleta() {
        return preenchido(nomePaciente) && preenchido(nomePrescritor) && preenchido(registroPrescritor) && preenchido(numeroReceita);
    }

    public DadosReceita build() {
        return new DadosReceita(nomePaciente.trim(), nomePrescritor.trim(), registroPrescritor.trim(), numeroReceita.trim());
    }

    private static boolean preenchido(String texto) {
        return texto != null && !texto.isBlank();
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public String getNomePrescritor() {
        return nomePrescritor;
    }

    public void setNomePrescritor(String nomePrescritor) {
        this.nomePrescritor = nomePrescritor;
    }

    public String getRegistroPrescritor() {
        return registroPrescritor;
    }

    public void setRegistroPrescritor(String registroPrescritor) {
        this.registroPrescritor = registroPrescritor;
    }

    public String getNumeroReceita() {
        return numeroReceita;
    }

    public void setNumeroReceita(String numeroReceita) {
        this.numeroReceita = numeroReceita;
    }
}
