package com.screenshot.service;

import com.microsoft.playwright.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebScrapingService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebScrapingService.class);
    

    /**
     * Extrai múltiplos textos de elementos
     */
    public List<String> extractMultipleTexts(String url, String selector) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Extraindo múltiplos textos do seletor '{}' em: {}", selector, url);
            page.navigate(url);
            page.waitForLoadState();
            
            // Aguarda pelo menos um elemento aparecer
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
            
            List<String> texts = page.locator(selector).allTextContents();
            browser.close();
            
            logger.info("Extraídos {} textos", texts.size());
            return texts;
        } catch (Exception e) {
            logger.error("Erro ao extrair múltiplos textos: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao extrair múltiplos textos", e);
        }
    }

}