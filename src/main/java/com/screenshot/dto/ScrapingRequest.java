package com.screenshot.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class ScrapingRequest {
    
    @NotBlank(message = "URL é obrigatória")
    private String url;
    
    private String selector;
    private List<String> attributes;
    private String script; // Para JavaScript personalizado
    
    // Construtores
    public ScrapingRequest() {}
    
    public ScrapingRequest(String url) {
        this.url = url;
    }
    
    public ScrapingRequest(String url, String selector) {
        this.url = url;
        this.selector = selector;
    }
    
    // Getters e Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getSelector() {
        return selector;
    }
    
    public void setSelector(String selector) {
        this.selector = selector;
    }
    
    public List<String> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }
    
    public String getScript() {
        return script;
    }
    
    public void setScript(String script) {
        this.script = script;
    }
}