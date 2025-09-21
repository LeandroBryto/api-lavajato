package com.screenshot.controller;

import com.screenshot.dto.ApiResponse;
import com.screenshot.dto.ScrapingRequest;
import com.screenshot.service.WebScrapingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/scraping")
@CrossOrigin(origins = "*")
public class WebScrapingController {

    private final WebScrapingService webScrapingService;

    @Autowired
    public WebScrapingController(WebScrapingService webScrapingService) {
        this.webScrapingService = webScrapingService;
    }


    @PostMapping("/texts")
    public ResponseEntity<ApiResponse<List<String>>> extractMultipleTexts(
            @Valid @RequestBody ScrapingRequest request) {
        
        List<String> texts = webScrapingService.extractMultipleTexts(request.getUrl(), request.getSelector());
        return ResponseEntity.ok(ApiResponse.success("Textos extraídos com sucesso", texts));
    }
}