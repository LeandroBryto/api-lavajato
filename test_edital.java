import com.microsoft.playwright.*;
import java.util.*;

public class test_edital {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            
            String url = "https://pncp.gov.br/app/editais/consulta-publica";
            System.out.println("Navegando para: " + url);
            
            page.navigate(url);
            page.waitForLoadState();
            page.waitForTimeout(10000); // Aguarda 10 segundos
            
            System.out.println("Título da página: " + page.title());
            System.out.println("URL atual: " + page.url());
            
            // Buscar por elementos que contenham palavras-chave
            String[] keywords = {"órgão", "objeto", "local", "modalidade", "última atualização"};
            
            for (String keyword : keywords) {
                System.out.println("\n=== Buscando por: " + keyword + " ===");
                
                try {
                    // Buscar elementos que contenham a palavra-chave
                    List<String> elements = page.locator("*:has-text('" + keyword + "')").allTextContents();
                    
                    int count = 0;
                    for (String element : elements) {
                        if (element != null && !element.trim().isEmpty() && 
                            element.toLowerCase().contains(keyword.toLowerCase()) && count < 5) {
                            System.out.println("Encontrado: " + element.trim().substring(0, Math.min(element.trim().length(), 100)));
                            count++;
                        }
                    }
                    
                    if (count == 0) {
                        System.out.println("Nenhum elemento encontrado para: " + keyword);
                    }
                    
                } catch (Exception e) {
                    System.out.println("Erro ao buscar " + keyword + ": " + e.getMessage());
                }
            }
            
            // Buscar por formulários e inputs
            System.out.println("\n=== FORMULÁRIOS E INPUTS ===");
            try {
                List<String> inputs = page.locator("input, select, textarea").allTextContents();
                System.out.println("Total de inputs encontrados: " + inputs.size());
                
                // Buscar por labels
                List<String> labels = page.locator("label").allTextContents();
                System.out.println("Total de labels encontrados: " + labels.size());
                for (int i = 0; i < Math.min(labels.size(), 10); i++) {
                    if (labels.get(i) != null && !labels.get(i).trim().isEmpty()) {
                        System.out.println("Label " + i + ": " + labels.get(i).trim());
                    }
                }
                
            } catch (Exception e) {
                System.out.println("Erro ao buscar formulários: " + e.getMessage());
            }
            
            // Buscar por tabelas
            System.out.println("\n=== TABELAS ===");
            try {
                List<String> tables = page.locator("table").allTextContents();
                System.out.println("Total de tabelas encontradas: " + tables.size());
                
                List<String> tableHeaders = page.locator("th, td").allTextContents();
                System.out.println("Total de células de tabela: " + tableHeaders.size());
                
                for (int i = 0; i < Math.min(tableHeaders.size(), 20); i++) {
                    if (tableHeaders.get(i) != null && !tableHeaders.get(i).trim().isEmpty()) {
                        String cell = tableHeaders.get(i).trim();
                        if (cell.toLowerCase().contains("órgão") || 
                            cell.toLowerCase().contains("objeto") || 
                            cell.toLowerCase().contains("local") ||
                            cell.toLowerCase().contains("modalidade")) {
                            System.out.println("Célula relevante: " + cell);
                        }
                    }
                }
                
            } catch (Exception e) {
                System.out.println("Erro ao buscar tabelas: " + e.getMessage());
            }
            
            // Capturar uma amostra do HTML
            System.out.println("\n=== AMOSTRA DO HTML ===");
            try {
                String html = page.content();
                String sample = html.length() > 2000 ? html.substring(0, 2000) + "..." : html;
                System.out.println(sample);
            } catch (Exception e) {
                System.out.println("Erro ao capturar HTML: " + e.getMessage());
            }
            
            browser.close();
            
        } catch (Exception e) {
            System.out.println("Erro geral: " + e.getMessage());
            e.printStackTrace();
        }
    }
}