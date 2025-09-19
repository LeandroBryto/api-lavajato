package com.screenshot.controller;

import com.screenshot.dto.ApiResponse;
import com.screenshot.dto.ScrapingRequest;
import com.screenshot.service.WebScrapingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scraping")
@CrossOrigin(origins = "*")
public class WebScrapingController {

    private static final Logger logger = LoggerFactory.getLogger(WebScrapingController.class);

    @Autowired
    private WebScrapingService webScrapingService;


    @PostMapping("/texts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> extractMultipleTexts(
            @Valid @RequestBody ScrapingRequest request) {
        try {
            if (request.getSelector() == null || request.getSelector().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Seletor é obrigatório para extração de múltiplos textos"));
            }

            List<String> texts = webScrapingService.extractMultipleTexts(request.getUrl(), request.getSelector());

            Map<String, Object> result = new HashMap<>();
            result.put("url", request.getUrl());
            result.put("selector", request.getSelector());
            result.put("texts", texts);
            result.put("count", texts.size());

            return ResponseEntity.ok(ApiResponse.success("Múltiplos textos extraídos com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao extrair múltiplos textos: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para extrair dados específicos de editais (órgão, objeto, local, última atualização)
     */
    @PostMapping("/edital-data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> extractEditalTexts(@RequestBody ScrapingRequest request) {
        try {
            logger.info("Recebida solicitação para extrair dados de edital: {}", request.getUrl());
            
            Map<String, Object> editalData = webScrapingService.extractEditalData(request.getUrl());
            
            return ResponseEntity.ok(ApiResponse.success("Dados do edital extraídos com sucesso", editalData));
            
        } catch (Exception e) {
            logger.error("Erro ao extrair dados do edital: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erro ao extrair dados do edital: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint para debug - visualizar HTML da página
     */
    @PostMapping("/debug-html")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugHtml(@RequestBody Map<String, String> request) {
        try {
            String url = request.get("url");
            if (url == null || url.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("URL é obrigatória"));
            }
            
            Map<String, Object> debugData = webScrapingService.debugPageStructure(url);
            return ResponseEntity.ok(ApiResponse.success("Debug HTML extraído com sucesso", debugData));
            
        } catch (Exception e) {
            logger.error("Erro ao fazer debug do HTML: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erro interno do servidor: " + e.getMessage()));
        }
    }
}