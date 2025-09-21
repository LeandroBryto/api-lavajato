package com.screenshot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar as informações completas de um edital do PNCP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PncpEditalResponse {
    
    private String numeroEdital;
    private String idContratacao;
    private String modalidade;
    private String ultimaAtualizacao;
    private String orgao;
    private String local;
    private String objeto;
    
    /**
     * Construtor para criar uma resposta de erro
     */
    public static PncpEditalResponse erro(String mensagem) {
        PncpEditalResponse response = new PncpEditalResponse();
        response.setObjeto("Erro: " + mensagem);
        return response;
    }
    
    /**
     * Verifica se o edital tem informações válidas
     */
    public boolean isValido() {
        return numeroEdital != null && !numeroEdital.trim().isEmpty() &&
               objeto != null && !objeto.trim().isEmpty();
    }
}