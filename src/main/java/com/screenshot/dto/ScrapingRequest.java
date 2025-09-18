package com.screenshot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapingRequest {
    
    @NotBlank(message = "URL é obrigatória")
    private String url;
    
    private String selector;
    private List<String> attributes;
    private String script;
    

    public ScrapingRequest(String url) {
        this.url = url;
    }
    
    public ScrapingRequest(String url, String selector) {
        this.url = url;
        this.selector = selector;
    }
    

}