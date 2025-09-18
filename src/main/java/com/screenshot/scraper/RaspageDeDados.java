package com.screenshot.scraper;

import com.microsoft.playwright.*;
import java.util.*;

public class RaspageDeDados {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            // URL alvo
            String url = "https://pncp.gov.br/app/editais?q=&status=recebendo_proposta&pagina=1&ufs=DF";
            page.navigate(url);

            // Espera carregar os cards
            page.waitForSelector("app-card-edital");

            // Seleciona todos os blocos de editais
            List<ElementHandle> editais = page.querySelectorAll("app-card-edital");

            List<Map<String, String>> resultados = new ArrayList<>();

            for (ElementHandle edital : editais) {
                Map<String, String> dados = new LinkedHashMap<>();

                // Pega os campos principais
                dados.put("Edital", edital.querySelector("span:has-text('Edital')") != null ? edital.querySelector("span:has-text('Edital')").innerText() : "");
                dados.put("IdContratacao", edital.querySelector("span:has-text('Id contratação')") != null ? edital.querySelector("span:has-text('Id contratação')").innerText() : "");
                dados.put("Modalidade", edital.querySelector("span:has-text('Modalidade')") != null ? edital.querySelector("span:has-text('Modalidade')").innerText() : "");
                dados.put("Atualizacao", edital.querySelector("span:has-text('Última Atualização')") != null ? edital.querySelector("span:has-text('Última Atualização')").innerText() : "");
                dados.put("Orgao", edital.querySelector("span:has-text('Órgão')") != null ? edital.querySelector("span:has-text('Órgão')").innerText() : "");
                dados.put("Local", edital.querySelector("span:has-text('Local')") != null ? edital.querySelector("span:has-text('Local')").innerText() : "");
                dados.put("Objeto", edital.querySelector("span:has-text('Objeto')") != null ? edital.querySelector("span:has-text('Objeto')").innerText() : "");

                resultados.add(dados);
            }

            // Exibir como JSON
            for (Map<String, String> edital : resultados) {
                System.out.println(edital);
            }

            browser.close();
        }
    }
}