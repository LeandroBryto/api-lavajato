import java.util.*;
import java.util.regex.Pattern;

public class test_edital_list {
    
    public static void main(String[] args) {
        // Simular o texto da página PNCP baseado na estrutura fornecida
        String pageText = """
        Editais e Avisos de Contratações A Receber/Recebendo Propostas
        Filtros ativos: UF: DF
        Exibindo: 10 de 571
        Ordenar por: Mais recente
        
        Aviso de Contratação Direta nº 12/2025
        Id contratação PNCP: 00394502000144-1-007237/2025
        Modalidade da Contratação: Dispensa
        Última Atualização: 18/09/2025
        Órgão: COMANDO DA MARINHA
        Local: Brasília/DF
        Objeto: Aquisição de Balcões térmicos para os refeitórios da Estação Rádio da Marinha em Brasília.
        
        Aviso de Contratação Direta nº 10/2025
        Id contratação PNCP: 00394502000144-1-007235/2025
        Modalidade da Contratação: Dispensa
        Última Atualização: 18/09/2025
        Órgão: COMANDO DA MARINHA
        Local: Brasília/DF
        Objeto: Contratação de empresa ou profissional especializado para o serviço de instalação.
        
        Aviso de Contratação Direta nº 13/2025
        Id contratação PNCP: 00348003000110-1-000706/2025
        Modalidade da Contratação: Dispensa
        Última Atualização: 18/09/2025
        Órgão: EMPRESA BRASILEIRA DE PESQUISA AGROPECUARIA
        Local: Brasília/DF
        Objeto: Aquisição de materiais de consumo para laboratórios da Embrapa Agroenergia.
        """;
        
        System.out.println("=== TESTE DE EXTRAÇÃO DE LISTA DE EDITAIS ===");
        
        List<Map<String, String>> editais = extractEditaisFromText(pageText);
        
        System.out.println("Total de editais encontrados: " + editais.size());
        System.out.println();
        
        for (int i = 0; i < editais.size(); i++) {
            Map<String, String> edital = editais.get(i);
            System.out.println("=== EDITAL " + (i + 1) + " ===");
            
            for (Map.Entry<String, String> entry : edital.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println();
        }
    }
    
    public static List<Map<String, String>> extractEditaisFromText(String text) {
        List<Map<String, String>> editais = new ArrayList<>();
        
        // Dividir o texto em linhas
        String[] lines = text.split("\\n");
        
        Map<String, String> currentEdital = null;
        
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            
            String cleanLine = line.trim();
            
            // Detectar início de um novo edital
            if (cleanLine.matches(".*(?:Aviso de Contratação|Edital).*nº.*\\d+/\\d+.*")) {
                // Salvar edital anterior se existir
                if (currentEdital != null && !currentEdital.isEmpty()) {
                    editais.add(new HashMap<>(currentEdital));
                }
                
                // Iniciar novo edital
                currentEdital = new HashMap<>();
                currentEdital.put("titulo", cleanLine);
                continue;
            }
            
            if (currentEdital != null) {
                // Extrair campos específicos
                if (cleanLine.startsWith("Id contratação PNCP:")) {
                    currentEdital.put("idContratacao", cleanLine.replace("Id contratação PNCP:", "").trim());
                } else if (cleanLine.startsWith("Modalidade da Contratação:")) {
                    currentEdital.put("modalidade", cleanLine.replace("Modalidade da Contratação:", "").trim());
                } else if (cleanLine.startsWith("Última Atualização:")) {
                    currentEdital.put("ultimaAtualizacao", cleanLine.replace("Última Atualização:", "").trim());
                } else if (cleanLine.startsWith("Órgão:")) {
                    currentEdital.put("orgao", cleanLine.replace("Órgão:", "").trim());
                } else if (cleanLine.startsWith("Local:")) {
                    currentEdital.put("local", cleanLine.replace("Local:", "").trim());
                } else if (cleanLine.startsWith("Objeto:")) {
                    currentEdital.put("objeto", cleanLine.replace("Objeto:", "").trim());
                }
            }
        }
        
        // Adicionar último edital se existir
        if (currentEdital != null && !currentEdital.isEmpty()) {
            editais.add(currentEdital);
        }
        
        return editais;
    }
}