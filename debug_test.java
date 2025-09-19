import com.microsoft.playwright.*;
import java.util.*;

public class debug_test {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            
            String url = "https://pncp.gov.br/app/editais/consulta-publica";
            System.out.println("Navegando para: " + url);
            
            page.navigate(url);
            page.waitForLoadState();
            page.waitForTimeout(5000);
            
            System.out.println("Título da página: " + page.title());
            
            // Buscar por elementos que contenham palavras-chave
            String[] keywords = {"Órgão", "Objeto", "Local", "Última Atualização", "Modalidade"};
            
            for (String keyword : keywords) {
                System.out.println("\n=== Buscando por: " + keyword + " ===");
                
                // Diferentes seletores para testar
                String[] selectors = {
                    "text=" + keyword,
                    "*:has-text('" + keyword + "')",
                    "div:has-text('" + keyword + "')",
                    "span:has-text('" + keyword + "')",
                    "td:has-text('" + keyword + "')",
                    "label:has-text('" + keyword + "')"
                };
                
                for (String selector : selectors) {
                    try {
                        List<String> texts = page.locator(selector).allTextContents();
                        if (!texts.isEmpty()) {
                            System.out.println("Seletor: " + selector);
                            for (int i = 0; i < Math.min(texts.size(), 3); i++) {
                                System.out.println("  - " + texts.get(i).trim());
                            }
                        }
                    } catch (Exception e) {
                        // Continuar
                    }
                }
            }
            
            // Capturar HTML de uma parte específica
            System.out.println("\n=== HTML Preview ===");
            String bodyHtml = page.locator("body").innerHTML();
            String preview = bodyHtml.length() > 2000 ? bodyHtml.substring(0, 2000) + "..." : bodyHtml;
            System.out.println(preview);
            
            browser.close();
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}