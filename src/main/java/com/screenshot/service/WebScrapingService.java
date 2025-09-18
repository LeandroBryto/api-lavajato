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
     * Extrai texto de um elemento específico
     */
    public String extractText(String url, String selector) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Extraindo texto do seletor '{}' em: {}", selector, url);
            page.navigate(url);
            page.waitForLoadState();
            
            // Aguarda o elemento aparecer
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
            
            String text = page.locator(selector).textContent();
            browser.close();
            
            logger.info("Texto extraído: {}", text != null ? text.substring(0, Math.min(100, text.length())) + "..." : "null");
            return text;
        } catch (Exception e) {
            logger.error("Erro ao extrair texto: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao extrair texto", e);
        }
    }
    
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
    
    /**
     * Extrai atributos de elementos
     */
    public Map<String, String> extractAttributes(String url, String selector, List<String> attributes) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Extraindo atributos {} do seletor '{}' em: {}", attributes, selector, url);
            page.navigate(url);
            page.waitForLoadState();
            
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
            
            Map<String, String> result = new HashMap<>();
            Locator element = page.locator(selector).first();
            
            for (String attribute : attributes) {
                String value = element.getAttribute(attribute);
                result.put(attribute, value);
            }
            
            browser.close();
            
            logger.info("Atributos extraídos: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Erro ao extrair atributos: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao extrair atributos", e);
        }
    }
    
    /**
     * Extrai informações completas da página
     */
    public Map<String, Object> extractPageInfo(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Extraindo informações completas da página: {}", url);
            page.navigate(url);
            page.waitForLoadState();
            
            Map<String, Object> pageInfo = new HashMap<>();
            
            // Título da página
            pageInfo.put("title", page.title());
            
            // URL atual
            pageInfo.put("url", page.url());
            
            // Meta description
            try {
                String description = page.locator("meta[name='description']").getAttribute("content");
                pageInfo.put("description", description);
            } catch (Exception e) {
                pageInfo.put("description", null);
            }
            
            // Todos os links
            List<String> links = new ArrayList<>();
            try {
                @SuppressWarnings("unchecked")
                List<String> hrefs = (List<String>) page.locator("a[href]").evaluateAll("elements => elements.map(el => el.href)");
                links.addAll(hrefs);
            } catch (Exception e) {
                logger.warn("Erro ao extrair links: {}", e.getMessage());
            }
            pageInfo.put("links", links);
            
            // Todas as imagens
            List<String> images = new ArrayList<>();
            try {
                @SuppressWarnings("unchecked")
                List<String> srcs = (List<String>) page.locator("img[src]").evaluateAll("elements => elements.map(el => el.src)");
                images.addAll(srcs);
            } catch (Exception e) {
                logger.warn("Erro ao extrair imagens: {}", e.getMessage());
            }
            pageInfo.put("images", images);
            
            // Headings
            Map<String, List<String>> headings = new HashMap<>();
            for (int i = 1; i <= 6; i++) {
                try {
                    List<String> h = page.locator("h" + i).allTextContents();
                    headings.put("h" + i, h);
                } catch (Exception e) {
                    headings.put("h" + i, new ArrayList<>());
                }
            }
            pageInfo.put("headings", headings);
            
            browser.close();
            
            logger.info("Informações da página extraídas com sucesso");
            return pageInfo;
        } catch (Exception e) {
            logger.error("Erro ao extrair informações da página: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao extrair informações da página", e);
        }
    }
    
    /**
     * Executa JavaScript personalizado na página
     */
    public Object executeScript(String url, String script) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Executando script personalizado em: {}", url);
            page.navigate(url);
            page.waitForLoadState();
            
            Object result = page.evaluate(script);
            browser.close();
            
            logger.info("Script executado com sucesso");
            return result;
        } catch (Exception e) {
            logger.error("Erro ao executar script: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao executar script", e);
        }
    }
    
    /**
     * Busca elementos por seletor CSS específico
     */
    public Map<String, Object> searchBySelector(String url, String selector) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Buscando elementos pelo seletor '{}' em: {}", selector, url);
            page.navigate(url);
            page.waitForLoadState();
            
            Map<String, Object> result = new HashMap<>();
            
            try {
                // Verifica se o elemento existe sem aguardar visibilidade
                Locator elements = page.locator(selector);
                int count = elements.count();
                
                // Se não encontrou elementos, tenta aguardar por elementos visíveis
                if (count == 0) {
                    try {
                        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(5000));
                        elements = page.locator(selector);
                        count = elements.count();
                    } catch (Exception e) {
                        logger.debug("Elemento '{}' não encontrado como visível, mas pode existir no DOM", selector);
                    }
                }
                
                if (count > 0) {
                    List<Map<String, Object>> foundElements = new ArrayList<>();
                    
                    for (int i = 0; i < count; i++) {
                        Locator element = elements.nth(i);
                        Map<String, Object> elementInfo = new HashMap<>();
                        
                        // Extrai informações do elemento
                        elementInfo.put("text", element.textContent());
                        elementInfo.put("innerHTML", element.innerHTML());
                        elementInfo.put("outerHTML", element.evaluate("el => el.outerHTML"));
                        
                        // Extrai atributos comuns
                        Map<String, String> attributes = new HashMap<>();
                        try {
                            attributes.put("id", element.getAttribute("id"));
                            attributes.put("class", element.getAttribute("class"));
                            attributes.put("href", element.getAttribute("href"));
                            attributes.put("src", element.getAttribute("src"));
                            attributes.put("alt", element.getAttribute("alt"));
                            attributes.put("title", element.getAttribute("title"));
                        } catch (Exception e) {
                            logger.warn("Erro ao extrair atributos do elemento {}: {}", i, e.getMessage());
                        }
                        elementInfo.put("attributes", attributes);
                        
                        foundElements.add(elementInfo);
                    }
                    
                    result.put("found", true);
                    result.put("count", count);
                    result.put("elements", foundElements);
                } else {
                    result.put("found", false);
                    result.put("count", 0);
                    result.put("elements", new ArrayList<>());
                }
                
            } catch (Exception e) {
                logger.warn("Elemento não encontrado ou timeout: {}", e.getMessage());
                result.put("found", false);
                result.put("count", 0);
                result.put("elements", new ArrayList<>());
                result.put("error", e.getMessage());
            }
            
            result.put("url", url);
            result.put("selector", selector);
            
            browser.close();
            
            logger.info("Busca concluída. Elementos encontrados: {}", result.get("count"));
            return result;
        } catch (Exception e) {
            logger.error("Erro ao buscar por seletor: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao buscar por seletor", e);
        }
    }
}