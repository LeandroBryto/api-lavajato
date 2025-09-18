package com.screenshot.controller;

import com.screenshot.dto.ApiResponse;
import com.screenshot.dto.ScreenshotRequest;
import com.screenshot.service.ScreenshotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/screenshot")
@CrossOrigin(origins = "*")
public class ScreenshotController {
    
    @Autowired
    private ScreenshotService screenshotService;
    
    /**
     * Captura screenshot simples
     */
    @PostMapping("/capture")
    public ResponseEntity<ApiResponse<Map<String, String>>> captureScreenshot(
            @Valid @RequestBody ScreenshotRequest request) {
        try {
            String filePath = screenshotService.captureScreenshot(request.getUrl(), request.getFileName());
            
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("url", request.getUrl());
            
            return ResponseEntity.ok(ApiResponse.success("Screenshot capturada com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao capturar screenshot: " + e.getMessage()));
        }
    }
    
    /**
     * Captura screenshot de página inteira
     */
    @PostMapping("/capture/fullpage")
    public ResponseEntity<ApiResponse<Map<String, String>>> captureFullPageScreenshot(
            @Valid @RequestBody ScreenshotRequest request) {
        try {
            String filePath = screenshotService.captureFullPageScreenshot(request.getUrl(), request.getFileName());
            
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("url", request.getUrl());
            result.put("type", "fullpage");
            
            return ResponseEntity.ok(ApiResponse.success("Screenshot de página inteira capturada com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao capturar screenshot de página inteira: " + e.getMessage()));
        }
    }
    
    /**
     * Captura screenshot em Base64
     */
    @PostMapping("/capture/base64")
    public ResponseEntity<ApiResponse<Map<String, String>>> captureScreenshotBase64(
            @Valid @RequestBody ScreenshotRequest request) {
        try {
            String base64 = screenshotService.captureScreenshotAsBase64(request.getUrl());
            
            Map<String, String> result = new HashMap<>();
            result.put("base64", base64);
            result.put("url", request.getUrl());
            result.put("format", "base64");
            
            return ResponseEntity.ok(ApiResponse.success("Screenshot em Base64 capturada com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao capturar screenshot em Base64: " + e.getMessage()));
        }
    }
    
    /**
     * Captura screenshot de elemento específico
     */
    @PostMapping("/capture/element")
    public ResponseEntity<ApiResponse<Map<String, String>>> captureElementScreenshot(
            @Valid @RequestBody ScreenshotRequest request) {
        try {
            if (request.getSelector() == null || request.getSelector().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Seletor é obrigatório para captura de elemento"));
            }
            
            String filePath = screenshotService.captureElementScreenshot(
                request.getUrl(), request.getSelector(), request.getFileName());
            
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("url", request.getUrl());
            result.put("selector", request.getSelector());
            result.put("type", "element");
            
            return ResponseEntity.ok(ApiResponse.success("Screenshot do elemento capturada com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao capturar screenshot do elemento: " + e.getMessage()));
        }
    }
    
    /**
     * Captura screenshot com opções personalizadas
     */
    @PostMapping("/capture/custom")
    public ResponseEntity<ApiResponse<Map<String, Object>>> captureCustomScreenshot(
            @Valid @RequestBody ScreenshotRequest request) {
        try {
            String filePath = screenshotService.captureCustomScreenshot(
                request.getUrl(),
                request.getFileName(),
                request.isFullPage(),
                request.getFormat(),
                request.getQuality(),
                request.getWidth(),
                request.getHeight()
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("url", request.getUrl());
            result.put("fullPage", request.isFullPage());
            result.put("format", request.getFormat());
            result.put("quality", request.getQuality());
            result.put("width", request.getWidth());
            result.put("height", request.getHeight());
            result.put("type", "custom");
            
            return ResponseEntity.ok(ApiResponse.success("Screenshot personalizada capturada com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro ao capturar screenshot personalizada: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint simples para teste rápido
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<Map<String, String>>> testScreenshot(
            @RequestParam String url,
            @RequestParam(required = false) String fileName) {
        try {
            String filePath = screenshotService.captureScreenshot(url, fileName);
            
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("url", url);
            result.put("method", "GET");
            
            return ResponseEntity.ok(ApiResponse.success("Screenshot de teste capturada com sucesso", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Erro no teste de screenshot: " + e.getMessage()));
        }
    }
}