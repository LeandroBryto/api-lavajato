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
    

}