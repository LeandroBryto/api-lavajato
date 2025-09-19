package com.screenshot.dto;

import java.util.List;

public class ScrapingRequest {
    
    private String url;
    private String selector;
    private List<String> attributes;
    private String script;
    
    // Construtores
    public ScrapingRequest() {}
    
    public ScrapingRequest(String url) {
        this.url = url;
    }
    
    public ScrapingRequest(String url, String selector) {
        this.url = url;
        this.selector = selector;
    }
    
    public ScrapingRequest(String url, String selector, List<String> attributes, String script) {
        this.url = url;
        this.selector = selector;
        this.attributes = attributes;
        this.script = script;
    }
    
    // Getters
    public String getUrl() {
        return url;
    }
    
    public String getSelector() {
        return selector;
    }
    
    public List<String> getAttributes() {
        return attributes;
    }
    
    public String getScript() {
        return script;
    }
    
    // Setters
    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setSelector(String selector) {
        this.selector = selector;
    }
    
    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }
    
    public void setScript(String script) {
        this.script = script;
    }
}