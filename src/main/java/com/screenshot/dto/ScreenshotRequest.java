package com.screenshot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class ScreenshotRequest {
    
    @NotBlank(message = "URL é obrigatória")
    private String url;
    
    private String fileName;
    private boolean fullPage = false;
    private String format = "png"; // png ou jpeg
    
    @Min(value = 1, message = "Qualidade deve ser entre 1 e 100")
    @Max(value = 100, message = "Qualidade deve ser entre 1 e 100")
    private Integer quality;
    
    @Min(value = 100, message = "Largura mínima é 100px")
    private Integer width;
    
    @Min(value = 100, message = "Altura mínima é 100px")
    private Integer height;
    
    private String selector; // Para captura de elemento específico
    
    // Construtores
    public ScreenshotRequest() {}
    
    public ScreenshotRequest(String url) {
        this.url = url;
    }
    
    // Getters e Setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public boolean isFullPage() {
        return fullPage;
    }
    
    public void setFullPage(boolean fullPage) {
        this.fullPage = fullPage;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public Integer getQuality() {
        return quality;
    }
    
    public void setQuality(Integer quality) {
        this.quality = quality;
    }
    
    public Integer getWidth() {
        return width;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public String getSelector() {
        return selector;
    }
    
    public void setSelector(String selector) {
        this.selector = selector;
    }
}