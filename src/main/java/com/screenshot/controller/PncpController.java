package com.screenshot.controller;

import com.screenshot.dto.ApiResponse;
import com.screenshot.dto.PncpEditalResponse;
import com.screenshot.service.PncpScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller especializado para endpoints do PNCP (Portal Nacional de Contratações Públicas)
 */
@RestController
@RequestMapping("/api/pncp")
@CrossOrigin(origins = "*")
public class PncpController {

    private final PncpScrapingService pncpScrapingService;

    @Autowired
    public PncpController(PncpScrapingService pncpScrapingService) {
        this.pncpScrapingService = pncpScrapingService;
    }

    /**
     * Endpoint para buscar todas as informações dos editais do PNCP
     * 
     * @param url URL da página do PNCP (opcional, usa URL padrão se não fornecida)
     * @param q Parâmetro de busca (opcional)
     * @param ufs Estados para filtrar (opcional)
     * @param pagina Número da página (opcional, padrão 1)
     * @param tamPagina Tamanho da página (opcional, padrão 100)
     * @return Lista com todas as informações dos editais encontrados
     */
    @GetMapping("/editais")
    public ResponseEntity<ApiResponse<List<PncpEditalResponse>>> buscarEditaisCompletos(
            @RequestParam(required = false) String url,
            @RequestParam(required = false, defaultValue = "gestao") String q,
            @RequestParam(required = false, defaultValue = "DF") String ufs,
            @RequestParam(required = false, defaultValue = "1") String pagina,
            @RequestParam(name = "tam_pagina", required = false, defaultValue = "100") String tamPagina) {
        
        try {
            // Se URL não foi fornecida, usar a URL padrão do PNCP
            String urlFinal = url;
            if (urlFinal == null || urlFinal.trim().isEmpty()) {
                urlFinal = String.format(
                    "https://pncp.gov.br/app/editais?pagina=%s&status=recebendo_proposta&tam_pagina=%s&q=%s&ufs=%s",
                    pagina, tamPagina, q, ufs
                );
            }
            
            List<PncpEditalResponse> editais = pncpScrapingService.extrairEditaisCompletos(urlFinal);
            
            String mensagem = String.format("Encontrados %d editais com informações completas", editais.size());
            return ResponseEntity.ok(ApiResponse.success(mensagem, editais));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Erro ao buscar editais: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para buscar informações específicas usando seletor customizado
     * 
     * @param url URL da página do PNCP
     * @param selector Seletor CSS para extrair informações específicas
     * @return Lista com as informações extraídas pelo seletor
     */
    @GetMapping("/extrair")
    public ResponseEntity<ApiResponse<List<String>>> extrairInformacoesPorSeletor(
            @RequestParam String url,
            @RequestParam String selector) {
        
        try {
            List<String> informacoes = pncpScrapingService.extrairInformacoesPorSeletor(url, selector);
            
            String mensagem = String.format("Extraídas %d informações usando o seletor: %s", 
                informacoes.size(), selector);
            return ResponseEntity.ok(ApiResponse.success(mensagem, informacoes));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Erro ao extrair informações: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para buscar editais com URL completa personalizada
     * 
     * @param urlCompleta URL completa da página do PNCP
     * @return Lista com todas as informações dos editais encontrados
     */
    @PostMapping("/editais/url-completa")
    public ResponseEntity<ApiResponse<List<PncpEditalResponse>>> buscarEditaisComUrlCompleta(
            @RequestBody String urlCompleta) {
        
        try {
            List<PncpEditalResponse> editais = pncpScrapingService.extrairEditaisCompletos(urlCompleta);
            
            String mensagem = String.format("Encontrados %d editais na URL fornecida", editais.size());
            return ResponseEntity.ok(ApiResponse.success(mensagem, editais));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Erro ao buscar editais: " + e.getMessage()));
        }
    }
}