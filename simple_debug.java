import com.microsoft.playwright.*;
import java.util.*;

public class simple_debug {
    public static void main(String[] args) {
        System.out.println("Iniciando debug do site PNCP...");
        
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            // URL de exemplo de um edital específico do PNCP
            String url = "https://pncp.gov.br/app/editais/consulta-publica";
            System.out.println("Navegando para: " + url);
            
            page.navigate(url);
            page.waitForLoadState();
            page.waitForTimeout(8000); // Aguarda mais tempo para carregamento
            
            System.out.println("Título da página: " + page.title());
            System.out.println("URL atual: " + page.url());
            
            // Verificar se a página carregou corretamente
            String pageContent = page.content();
            System.out.println("Tamanho do HTML: " + pageContent.length() + " caracteres");
            
            // Buscar por textos que possam indicar campos de edital
            List<String> allTexts = page.locator("*").allTextContents();
            System.out.println("Total de elementos com texto: " + allTexts.size());
            
            // Filtrar textos relevantes
            List<String> relevantTexts = new ArrayList<>();
            for (String text : allTexts) {
                if (text != null && !text.trim().isEmpty()) {
                    String lowerText = text.toLowerCase();
                    if (lowerText.contains("órgão") || 
                        lowerText.contains("objeto") || 
                        lowerText.contains("local") ||
                        lowerText.contains("última") ||
                        lowerText.contains("atualização") ||
                        lowerText.contains("modalidade")) {
                        relevantTexts.add(text.trim());
                    }
                }
            }
            
            System.out.println("\n=== TEXTOS RELEVANTES ENCONTRADOS ===");
            for (int i = 0; i < Math.min(relevantTexts.size(), 20); i++) {
                System.out.println((i+1) + ". " + relevantTexts.get(i));
            }
            
            // Verificar estrutura de tabelas
            List<String> tables = page.locator("table").allTextContents();
            System.out.println("\n=== TABELAS ENCONTRADAS ===");
            System.out.println("Número de tabelas: " + tables.size());
            
            // Verificar divs com classes específicas
            System.out.println("\n=== DIVS COM CLASSES ESPECÍFICAS ===");
            String[] classPatterns = {"edital", "orgao", "objeto", "local", "modalidade", "info", "dados", "detalhes"};
            for (String pattern : classPatterns) {
                try {
                    List<String> elements = page.locator("div[class*='" + pattern + "']").allTextContents();
                    if (!elements.isEmpty()) {
                        System.out.println("Divs com classe contendo '" + pattern + "': " + elements.size());
                        for (int i = 0; i < Math.min(elements.size(), 3); i++) {
                            System.out.println("  - " + elements.get(i).substring(0, Math.min(100, elements.get(i).length())) + "...");
                        }
                    }
                } catch (Exception e) {
                    // Continuar
                }
            }
            
            // Mostrar uma amostra do HTML
            System.out.println("\n=== AMOSTRA DO HTML ===");
            String htmlSample = pageContent.length() > 3000 ? pageContent.substring(0, 3000) + "..." : pageContent;
            System.out.println(htmlSample);
            
            browser.close();
            System.out.println("\nDebug concluído!");
            
        } catch (Exception e) {
            System.err.println("Erro durante o debug: " + e.getMessage());
            e.printStackTrace();
        }
    }
}