package com.screenshot.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ScreenshotType;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class ScreenshotService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotService.class);
    
    /**
     * Captura uma screenshot simples e salva em arquivo
     */
    public String captureScreenshot(String url, String fileName) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Navegando para: {}", url);
            page.navigate(url);
            
            // Aguarda o carregamento da página
            page.waitForLoadState();
            
            Path screenshotPath = Paths.get(fileName != null ? fileName : "screenshot.png");
            page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath));
            
            logger.info("Screenshot salva em: {}", screenshotPath.toAbsolutePath());
            browser.close();
            
            return screenshotPath.toAbsolutePath().toString();
        } catch (Exception e) {
            logger.error("Erro ao capturar screenshot: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao capturar screenshot", e);
        }
    }
    
    /**
     * Captura uma screenshot de página inteira
     */
    public String captureFullPageScreenshot(String url, String fileName) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Navegando para página inteira: {}", url);
            page.navigate(url);
            page.waitForLoadState();
            
            Path screenshotPath = Paths.get(fileName != null ? fileName : "fullpage_screenshot.png");
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));
            
            logger.info("Screenshot de página inteira salva em: {}", screenshotPath.toAbsolutePath());
            browser.close();
            
            return screenshotPath.toAbsolutePath().toString();
        } catch (Exception e) {
            logger.error("Erro ao capturar screenshot de página inteira: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao capturar screenshot de página inteira", e);
        }
    }
    
    /**
     * Captura screenshot em buffer (Base64)
     */
    public String captureScreenshotAsBase64(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Capturando screenshot em buffer para: {}", url);
            page.navigate(url);
            page.waitForLoadState();
            
            byte[] buffer = page.screenshot();
            String base64Screenshot = Base64.getEncoder().encodeToString(buffer);
            
            logger.info("Screenshot capturada em Base64, tamanho: {} bytes", buffer.length);
            browser.close();
            
            return base64Screenshot;
        } catch (Exception e) {
            logger.error("Erro ao capturar screenshot em Base64: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao capturar screenshot em Base64", e);
        }
    }
    
    /**
     * Captura screenshot de um elemento específico
     */
    public String captureElementScreenshot(String url, String selector, String fileName) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            logger.info("Navegando para capturar elemento '{}' em: {}", selector, url);
            page.navigate(url);
            page.waitForLoadState();
            
            // Aguarda o elemento aparecer
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(10000));
            
            Path screenshotPath = Paths.get(fileName != null ? fileName : "element_screenshot.png");
            page.locator(selector).screenshot(new Locator.ScreenshotOptions().setPath(screenshotPath));
            
            logger.info("Screenshot do elemento salva em: {}", screenshotPath.toAbsolutePath());
            browser.close();
            
            return screenshotPath.toAbsolutePath().toString();
        } catch (Exception e) {
            logger.error("Erro ao capturar screenshot do elemento: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao capturar screenshot do elemento", e);
        }
    }
    
    /**
     * Captura screenshot com opções personalizadas
     */
    public String captureCustomScreenshot(String url, String fileName, boolean fullPage, 
                                        String format, Integer quality, Integer width, Integer height) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            
            // Configurar viewport se especificado
            Page page;
            if (width != null && height != null) {
                page = browser.newPage(new Browser.NewPageOptions()
                    .setViewportSize(width, height));
            } else {
                page = browser.newPage();
            }
            
            logger.info("Navegando para captura personalizada: {}", url);
            page.navigate(url);
            page.waitForLoadState();
            
            Path screenshotPath = Paths.get(fileName != null ? fileName : "custom_screenshot.png");
            
            Page.ScreenshotOptions options = new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(fullPage);
            
            // Configurar formato
            if ("jpeg".equalsIgnoreCase(format) || "jpg".equalsIgnoreCase(format)) {
                options.setType(ScreenshotType.JPEG);
                if (quality != null) {
                    options.setQuality(quality);
                }
            } else {
                options.setType(ScreenshotType.PNG);
            }
            
            page.screenshot(options);
            
            logger.info("Screenshot personalizada salva em: {}", screenshotPath.toAbsolutePath());
            browser.close();
            
            return screenshotPath.toAbsolutePath().toString();
        } catch (Exception e) {
            logger.error("Erro ao capturar screenshot personalizada: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao capturar screenshot personalizada", e);
        }
    }
}