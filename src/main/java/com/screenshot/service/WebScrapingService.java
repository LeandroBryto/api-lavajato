package com.screenshot.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WebScrapingService {

    @Value("${app.scraping.timeout:30000}")
    private int scrapingTimeout;


    public List<String> extractMultipleTexts(String url, String selector) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            page.setDefaultTimeout(scrapingTimeout);
            
            // Navegar para a URL
            page.navigate(url);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            
            List<String> texts = page.locator(selector).allTextContents();
            
            browser.close();
            return texts;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair textos da página: " + e.getMessage(), e);
        }
    }
}