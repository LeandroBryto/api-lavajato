package com.screenshot.controller;

import com.screenshot.dto.ApiResponse;
import com.screenshot.dto.ScrapingRequest;
import com.screenshot.service.WebScrapingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scraping")
@CrossOrigin(origins = "*")
public class WebScrapingController {
    
    @Autowired
    private WebScrapingService webScrapingService;
    

    @PostMapping("/text")
    public ResponseEntity<ApiResponse<Map<String, Object>>> extractText(
            @Valid @RequestBody ScrapingRequest request) {
        try {
            if (request.getSelector() == null || request.getSelector().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Seletor é obrigatório para extração de texto"));
            }
            
            String text = webScrapingService.extractText(request.getUrl(), request.getSelector());
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", request.getUrl());
            result.put("selector", request.getSelector());
            result.put("text", text);
            result.put("length", text != null ? text.length() : 0);
            
            return ResponseEntity.ok(ApiResponse.success("Texto extraído com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao extrair texto: " + e.getMessage()));
        }
    }
    

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
     * Extrai atributos de elementos
     */
    @PostMapping("/attributes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> extractAttributes(
            @Valid @RequestBody ScrapingRequest request) {
        try {
            if (request.getSelector() == null || request.getSelector().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Seletor é obrigatório para extração de atributos"));
            }
            
            if (request.getAttributes() == null || request.getAttributes().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Lista de atributos é obrigatória"));
            }
            
            Map<String, String> attributes = webScrapingService.extractAttributes(
                request.getUrl(), request.getSelector(), request.getAttributes());
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", request.getUrl());
            result.put("selector", request.getSelector());
            result.put("requestedAttributes", request.getAttributes());
            result.put("attributes", attributes);
            
            return ResponseEntity.ok(ApiResponse.success("Atributos extraídos com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao extrair atributos: " + e.getMessage()));
        }
    }
    
    /**
     * Extrai informações completas da página
     */
    @PostMapping("/page-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> extractPageInfo(
            @Valid @RequestBody ScrapingRequest request) {
        try {
            Map<String, Object> pageInfo = webScrapingService.extractPageInfo(request.getUrl());
            
            return ResponseEntity.ok(ApiResponse.success("Informações da página extraídas com sucesso", pageInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao extrair informações da página: " + e.getMessage()));
        }
    }
    
    /**
     * Executa JavaScript personalizado
     */
    @PostMapping("/execute-script")
    public ResponseEntity<ApiResponse<Map<String, Object>>> executeScript(
            @Valid @RequestBody ScrapingRequest request) {
        try {
            if (request.getScript() == null || request.getScript().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Script JavaScript é obrigatório"));
            }
            
            Object scriptResult = webScrapingService.executeScript(request.getUrl(), request.getScript());
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", request.getUrl());
            result.put("script", request.getScript());
            result.put("result", scriptResult);
            result.put("resultType", scriptResult != null ? scriptResult.getClass().getSimpleName() : "null");
            
            return ResponseEntity.ok(ApiResponse.success("Script executado com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao executar script: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint simples para teste rápido de extração de texto
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testScraping(
            @RequestParam String url,
            @RequestParam String selector) {
        try {
            String text = webScrapingService.extractText(url, selector);
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("selector", selector);
            result.put("text", text);
            result.put("method", "GET");
            
            return ResponseEntity.ok(ApiResponse.success("Teste de raspagem realizado com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro no teste de raspagem: " + e.getMessage()));
        }
    }
    
    /**
     * Busca elementos por seletor CSS específico
     */
    @PostMapping("/search-by-selector")
    public ResponseEntity<ApiResponse<Map<String, Object>>> searchBySelector(
            @Valid @RequestBody ScrapingRequest request) {
        try {
            if (request.getSelector() == null || request.getSelector().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Seletor é obrigatório"));
            }
            
            Map<String, Object> result = webScrapingService.searchBySelector(
                request.getUrl(), request.getSelector());
            
            return ResponseEntity.ok(ApiResponse.success("Busca realizada com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao buscar elementos: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint de diagnóstico para verificar problemas com seletores
     */
    @PostMapping("/diagnose-selector")
    public ResponseEntity<ApiResponse<Map<String, Object>>> diagnoseSelector(
            @Valid @RequestBody ScrapingRequest request) {
        Map<String, Object> diagnosis = new HashMap<>();
        diagnosis.put("url", request.getUrl());
        diagnosis.put("selector", request.getSelector());
        
        // Verifica se é um seletor problemático
        if ("title".equals(request.getSelector())) {
            diagnosis.put("problem", "O seletor 'title' refere-se ao elemento <title> no <head> da página");
            diagnosis.put("reason", "Este elemento não é visível no DOM e causa TimeoutError no Playwright");
            diagnosis.put("solution", "Use um seletor para elementos visíveis como 'h1', 'p', 'div', etc.");
            diagnosis.put("status", "PROBLEMA_IDENTIFICADO");
        } else {
            diagnosis.put("status", "SELETOR_OK");
            diagnosis.put("message", "Este seletor deve funcionar se o elemento existir na página");
        }
        
        return ResponseEntity.ok(ApiResponse.success("Diagnóstico concluído", diagnosis));
    }
}