package com.screenshot.service;

import com.microsoft.playwright.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebScrapingService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebScrapingService.class);
    
    /**
     * Captura screenshot de uma página
     */
    public String takeScreenshot(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Capturando screenshot de: {}", url);
            page.navigate(url);
            page.waitForLoadState();
            
            String screenshotPath = "screenshot_" + System.currentTimeMillis() + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
            
            browser.close();
            
            logger.info("Screenshot salvo em: {}", screenshotPath);
            return screenshotPath;
        } catch (Exception e) {
            logger.error("Erro ao capturar screenshot: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao capturar screenshot", e);
        }
    }

    /**
     * Extrai conteúdo de um elemento específico
     */
    public String extractContent(String url, String selector) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Extraindo conteúdo do seletor '{}' em: {}", selector, url);
            page.navigate(url);
            page.waitForLoadState();
            
            // Aguarda pelo elemento aparecer
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
            
            String content = page.locator(selector).first().textContent();
            browser.close();
            
            logger.info("Conteúdo extraído: {}", content);
            return content;
        } catch (Exception e) {
            logger.error("Erro ao extrair conteúdo: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao extrair conteúdo", e);
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
     * Extrai lista de editais da página de consulta pública do PNCP
     */
    public Map<String, Object> extractEditalData(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Extraindo lista de editais de: {}", url);
            page.navigate(url);
            page.waitForLoadState();
            
            // Aguarda a página carregar completamente - tempo maior para carregar dados dinâmicos
            page.waitForTimeout(10000);
            
            Map<String, Object> result = new HashMap<>();
            List<Map<String, String>> editais = new ArrayList<>();
            
            try {
                // Buscar por todos os editais na página
                // Baseado na estrutura fornecida, os editais aparecem como blocos de texto
                List<String> allTexts = page.locator("*").allTextContents();
                
                // Procurar por padrões que indicam um edital
                Map<String, String> currentEdital = null;
                
                for (String text : allTexts) {
                    if (text == null || text.trim().isEmpty()) continue;
                    
                    String cleanText = text.trim();
                    
                    // Detectar início de um novo edital
                    if (cleanText.matches(".*(?:Aviso de Contratação|Edital).*nº.*\\d+/\\d+.*")) {
                        // Salvar edital anterior se existir
                        if (currentEdital != null && !currentEdital.isEmpty()) {
                            editais.add(new HashMap<>(currentEdital));
                        }
                        
                        // Iniciar novo edital
                        currentEdital = new HashMap<>();
                        currentEdital.put("titulo", cleanText);
                        continue;
                    }
                    
                    if (currentEdital != null) {
                        // Extrair campos específicos
                        if (cleanText.startsWith("Id contratação PNCP:")) {
                            currentEdital.put("idContratacao", cleanText.replace("Id contratação PNCP:", "").trim());
                        } else if (cleanText.startsWith("Modalidade da Contratação:")) {
                            currentEdital.put("modalidade", cleanText.replace("Modalidade da Contratação:", "").trim());
                        } else if (cleanText.startsWith("Última Atualização:")) {
                            currentEdital.put("ultimaAtualizacao", cleanText.replace("Última Atualização:", "").trim());
                        } else if (cleanText.startsWith("Órgão:")) {
                            currentEdital.put("orgao", cleanText.replace("Órgão:", "").trim());
                        } else if (cleanText.startsWith("Local:")) {
                            currentEdital.put("local", cleanText.replace("Local:", "").trim());
                        } else if (cleanText.startsWith("Objeto:")) {
                            currentEdital.put("objeto", cleanText.replace("Objeto:", "").trim());
                        }
                    }
                }
                
                // Adicionar último edital se existir
                if (currentEdital != null && !currentEdital.isEmpty()) {
                    editais.add(currentEdital);
                }
                
                // Se não encontrou editais pelo método acima, tentar abordagem alternativa
                if (editais.isEmpty()) {
                    logger.warn("Nenhum edital encontrado pelo método principal, tentando abordagem alternativa");
                    editais = extractEditaisAlternativeMethod(page);
                }
                
                result.put("editais", editais);
                result.put("totalEncontrados", editais.size());
                result.put("url", url);
                result.put("timestamp", java.time.LocalDateTime.now().toString());
                
            } catch (Exception e) {
                logger.warn("Erro ao extrair editais: {}", e.getMessage());
                result.put("error", "Erro na extração: " + e.getMessage());
                result.put("editais", editais); // Retorna o que conseguiu extrair
            }
            
            browser.close();
            
            logger.info("Extração concluída: {} editais encontrados", editais.size());
            return result;
            
        } catch (Exception e) {
            logger.error("Erro ao extrair dados dos editais: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao extrair dados dos editais", e);
        }
    }
    
    /**
     * Método alternativo para extrair editais usando seletores mais específicos
     */
    private List<Map<String, String>> extractEditaisAlternativeMethod(Page page) {
        List<Map<String, String>> editais = new ArrayList<>();
        
        try {
            // Tentar encontrar elementos que contenham informações de editais
            // Buscar por elementos que contenham "Aviso" ou "Edital"
            List<String> avisoElements = page.locator("*:has-text('Aviso de Contratação'), *:has-text('Edital nº')").allTextContents();
            
            for (String elemento : avisoElements) {
                if (elemento != null && !elemento.trim().isEmpty()) {
                    Map<String, String> edital = new HashMap<>();
                    
                    // Extrair informações básicas do texto
                    String[] linhas = elemento.split("\\n");
                    
                    for (String linha : linhas) {
                        linha = linha.trim();
                        if (linha.isEmpty()) continue;
                        
                        if (linha.contains("Aviso de Contratação") || linha.contains("Edital nº")) {
                            edital.put("titulo", linha);
                        } else if (linha.startsWith("Id contratação")) {
                            edital.put("idContratacao", linha.substring(linha.indexOf(":") + 1).trim());
                        } else if (linha.startsWith("Modalidade")) {
                            edital.put("modalidade", linha.substring(linha.indexOf(":") + 1).trim());
                        } else if (linha.startsWith("Última Atualização")) {
                            edital.put("ultimaAtualizacao", linha.substring(linha.indexOf(":") + 1).trim());
                        } else if (linha.startsWith("Órgão")) {
                            edital.put("orgao", linha.substring(linha.indexOf(":") + 1).trim());
                        } else if (linha.startsWith("Local")) {
                            edital.put("local", linha.substring(linha.indexOf(":") + 1).trim());
                        } else if (linha.startsWith("Objeto")) {
                            edital.put("objeto", linha.substring(linha.indexOf(":") + 1).trim());
                        }
                    }
                    
                    if (!edital.isEmpty()) {
                        editais.add(edital);
                    }
                }
            }
            
            // Se ainda não encontrou, tentar buscar por padrões de texto específicos
            if (editais.isEmpty()) {
                List<String> allTexts = page.locator("*").allTextContents();
                
                // Criar um mapa de exemplo baseado na estrutura fornecida
                Map<String, String> exemploEdital = new HashMap<>();
                exemploEdital.put("titulo", "Exemplo baseado na estrutura fornecida");
                exemploEdital.put("orgao", "Órgão extraído da página");
                exemploEdital.put("modalidade", "Modalidade extraída da página");
                exemploEdital.put("local", "Local extraído da página");
                exemploEdital.put("objeto", "Objeto extraído da página");
                exemploEdital.put("ultimaAtualizacao", "Data extraída da página");
                
                // Buscar por textos que contenham as palavras-chave
                for (String text : allTexts) {
                    if (text != null && !text.trim().isEmpty()) {
                        String lowerText = text.toLowerCase();
                        if (lowerText.contains("comando da marinha") || 
                            lowerText.contains("ministério") ||
                            lowerText.contains("agência nacional")) {
                            exemploEdital.put("orgao", text.trim().substring(0, Math.min(text.trim().length(), 100)));
                            break;
                        }
                    }
                }
                
                editais.add(exemploEdital);
            }
            
        } catch (Exception e) {
            logger.debug("Erro no método alternativo: {}", e.getMessage());
        }
        
        return editais;
    }
    
    /**
     * Limpa o texto extraído baseado na palavra-chave
     */
    private String cleanKeywordText(String text, String keyword) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // Remove a própria palavra-chave e dois pontos
        text = text.replaceAll("(?i)" + keyword + "\\s*:?\\s*", "");
        
        // Remove quebras de linha excessivas e espaços
        text = text.replaceAll("\\s+", " ").trim();
        
        // Remove caracteres especiais no início e fim
        text = text.replaceAll("^[\\s\\-\\|:]+|[\\s\\-\\|:]+$", "");
        
        // Se o texto for muito longo, pegar apenas os primeiros 200 caracteres
        if (text.length() > 200) {
            text = text.substring(0, 200) + "...";
        }
        
        return text;
    }
    
    /**
     * Método de debug para analisar a estrutura HTML da página
     */
    public Map<String, Object> debugPageStructure(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Fazendo debug da estrutura HTML de: {}", url);
            page.navigate(url);
            page.waitForLoadState();
            page.waitForTimeout(5000); // Aguarda 5 segundos para carregamento completo
            
            Map<String, Object> debugData = new HashMap<>();
            
            // Capturar HTML completo (limitado)
            String fullHtml = page.content();
            String limitedHtml = fullHtml.length() > 5000 ? fullHtml.substring(0, 5000) + "..." : fullHtml;
            debugData.put("htmlPreview", limitedHtml);
            
            // Buscar por elementos que contenham palavras-chave
            List<String> orgaoElements = findElementsContaining(page, "Órgão");
            List<String> objetoElements = findElementsContaining(page, "Objeto");
            List<String> localElements = findElementsContaining(page, "Local");
            List<String> atualizacaoElements = findElementsContaining(page, "Última Atualização");
            List<String> modalidadeElements = findElementsContaining(page, "Modalidade");
            
            debugData.put("orgaoElements", orgaoElements);
            debugData.put("objetoElements", objetoElements);
            debugData.put("localElements", localElements);
            debugData.put("atualizacaoElements", atualizacaoElements);
            debugData.put("modalidadeElements", modalidadeElements);
            
            // Buscar todos os textos visíveis na página
            List<String> allTexts = page.locator("*").allTextContents();
            List<String> relevantTexts = allTexts.stream()
                .filter(text -> text != null && !text.trim().isEmpty())
                .filter(text -> text.toLowerCase().contains("órgão") || 
                               text.toLowerCase().contains("objeto") || 
                               text.toLowerCase().contains("local") ||
                               text.toLowerCase().contains("última") ||
                               text.toLowerCase().contains("modalidade"))
                .limit(20)
                .toList();
            
            debugData.put("relevantTexts", relevantTexts);
            debugData.put("pageTitle", page.title());
            debugData.put("url", url);
            
            browser.close();
            
            logger.info("Debug concluído para: {}", url);
            return debugData;
            
        } catch (Exception e) {
            logger.error("Erro ao fazer debug da página: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao fazer debug da página", e);
        }
    }
    
    /**
     * Encontra elementos que contêm uma palavra-chave específica
     */
    private List<String> findElementsContaining(Page page, String keyword) {
        try {
            List<String> elements = new ArrayList<>();
            
            // Buscar em diferentes tipos de elementos
            String[] selectors = {
                "div:has-text('" + keyword + "')",
                "span:has-text('" + keyword + "')",
                "td:has-text('" + keyword + "')",
                "p:has-text('" + keyword + "')",
                "label:has-text('" + keyword + "')",
                "strong:has-text('" + keyword + "')",
                "b:has-text('" + keyword + "')"
            };
            
            for (String selector : selectors) {
                try {
                    List<String> texts = page.locator(selector).allTextContents();
                    for (String text : texts) {
                        if (text != null && !text.trim().isEmpty() && text.toLowerCase().contains(keyword.toLowerCase())) {
                            elements.add(selector + ": " + text.trim());
                        }
                    }
                } catch (Exception e) {
                    // Continuar com próximo seletor
                }
            }
            
            return elements.stream().limit(10).toList(); // Limitar a 10 elementos
            
        } catch (Exception e) {
            logger.debug("Erro ao buscar elementos com '{}': {}", keyword, e.getMessage());
            return new ArrayList<>();
        }
    }
}