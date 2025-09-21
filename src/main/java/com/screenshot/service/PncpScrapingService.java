package com.screenshot.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.screenshot.dto.PncpEditalResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service especializado para extrair informações dos editais do PNCP
 */
@Service
public class PncpScrapingService {

    @Value("${app.scraping.timeout:30000}")
    private int scrapingTimeout;

    /**
     * Extrai todas as informações dos editais da página do PNCP
     */
    public List<PncpEditalResponse> extrairEditaisCompletos(String url) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            page.setDefaultTimeout(scrapingTimeout);
            
            // Navegar para a URL
            page.navigate(url);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            
            List<PncpEditalResponse> editais = new ArrayList<>();
            
            // Localizar todos os itens de edital
            var itensEdital = page.locator("a.br-item");
            int count = itensEdital.count();
            
            for (int i = 0; i < count; i++) {
                try {
                    var item = itensEdital.nth(i);
                    PncpEditalResponse edital = extrairInformacoesDoItem(item);
                    if (edital.isValido()) {
                        editais.add(edital);
                    }
                } catch (Exception e) {
                    // Se falhar em um item, continua com os outros
                    System.err.println("Erro ao processar item " + i + ": " + e.getMessage());
                }
            }
            
            browser.close();
            return editais;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair editais da página PNCP: " + e.getMessage(), e);
        }
    }

    /**
     * Extrai informações de um item específico de edital
     */
    private PncpEditalResponse extrairInformacoesDoItem(Locator item) {
        try {
            // Extrair número do edital
            String numeroEdital = "";
            try {
                var numeroElement = item.locator("div.mb-1 strong:has-text('Edital nº')");
                numeroEdital = numeroElement.textContent().trim();
                numeroEdital = numeroEdital.replace("Edital nº", "").trim();
            } catch (Exception e) {
                numeroEdital = "N/A";
            }
            
            // Extrair ID de contratação
            String idContratacao = "";
            try {
                var idElement = item.locator("div.mb-1 span:has-text('Id contratação PNCP:')");
                idContratacao = idElement.textContent().trim();
                idContratacao = idContratacao.replace("Id contratação PNCP:", "").trim();
            } catch (Exception e) {
                idContratacao = "N/A";
            }
            
            // Extrair modalidade
            String modalidade = "";
            try {
                var modalidadeElement = item.locator("div.col-auto.mb-1:has-text('Modalidade da Contratação:')");
                modalidade = modalidadeElement.textContent().trim();
                modalidade = modalidade.replace("Modalidade da Contratação:", "").trim();
            } catch (Exception e) {
                modalidade = "N/A";
            }
            
            // Extrair última atualização
            String ultimaAtualizacao = "";
            try {
                var atualizacaoElement = item.locator("div.col-auto.mb-1:has-text('Última Atualização:')");
                ultimaAtualizacao = atualizacaoElement.textContent().trim();
                ultimaAtualizacao = ultimaAtualizacao.replace("Última Atualização:", "").trim();
            } catch (Exception e) {
                ultimaAtualizacao = "N/A";
            }
            
            // Extrair órgão
            String orgao = "";
            try {
                var orgaoElement = item.locator("div.col-auto.mb-1 span:has-text('Órgão:')");
                orgao = orgaoElement.textContent().trim();
                orgao = orgao.replace("Órgão:", "").trim();
            } catch (Exception e) {
                orgao = "N/A";
            }
            
            // Extrair local
            String local = "";
            try {
                var localElement = item.locator("div.col-auto.mb-1 span:has-text('Local:')");
                local = localElement.textContent().trim();
                local = local.replace("Local:", "").trim();
            } catch (Exception e) {
                local = "N/A";
            }
            
            // Extrair objeto
            String objeto = "";
            try {
                var objetoElement = item.locator("span:has-text('Objeto:')");
                objeto = objetoElement.textContent().trim();
                objeto = objeto.replace("Objeto:", "").trim();
            } catch (Exception e) {
                objeto = "N/A";
            }
            
            return new PncpEditalResponse(numeroEdital, idContratacao, modalidade, 
                                        ultimaAtualizacao, orgao, local, objeto);
            
        } catch (Exception e) {
            return new PncpEditalResponse("", "", "", "", "", "", "");
        }
    }

    /**
     * Extrai informações usando um seletor específico
     */
    public List<String> extrairInformacoesPorSeletor(String url, String seletor) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            page.setDefaultTimeout(scrapingTimeout);
            
            // Navegar para a URL
            page.navigate(url);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            
            // Extrair informações usando o seletor fornecido
            List<String> resultados = new ArrayList<>();
            
            try {
                var elementos = page.locator(seletor);
                int count = elementos.count();
                
                for (int i = 0; i < count; i++) {
                    try {
                        String texto = elementos.nth(i).textContent();
                        if (texto != null && !texto.trim().isEmpty()) {
                            resultados.add(texto.trim());
                        }
                    } catch (Exception e) {
                        // Continua com o próximo elemento se houver erro
                        System.err.println("Erro ao extrair elemento " + i + ": " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao localizar elementos com seletor '" + seletor + "': " + e.getMessage());
            }
            
            browser.close();
            return resultados;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair informações por seletor: " + e.getMessage(), e);
        }
    }

    /**
     * Extrai múltiplas informações usando diferentes seletores em uma única sessão
     */
    public List<List<String>> extrairMultiplasInformacoes(String url, List<String> seletores) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            page.setDefaultTimeout(scrapingTimeout);
            
            // Navegar para a URL
            page.navigate(url);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            
            List<List<String>> resultados = new ArrayList<>();
            
            for (String seletor : seletores) {
                List<String> textosSeletor = new ArrayList<>();
                
                try {
                    var elementos = page.locator(seletor);
                    int count = elementos.count();
                    
                    for (int i = 0; i < count; i++) {
                        try {
                            String texto = elementos.nth(i).textContent();
                            if (texto != null && !texto.trim().isEmpty()) {
                                textosSeletor.add(texto.trim());
                            }
                        } catch (Exception e) {
                            System.err.println("Erro ao extrair elemento " + i + " do seletor '" + seletor + "': " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar seletor '" + seletor + "': " + e.getMessage());
                }
                
                resultados.add(textosSeletor);
            }
            
            browser.close();
            return resultados;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair múltiplas informações: " + e.getMessage(), e);
        }
    }
}