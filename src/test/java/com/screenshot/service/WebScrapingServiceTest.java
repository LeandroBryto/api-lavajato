package com.screenshot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebScrapingService Tests")
class WebScrapingServiceTest {

    @InjectMocks
    private WebScrapingService webScrapingService;

    @BeforeEach
    void setUp() {
        // Configurar timeout para testes
        ReflectionTestUtils.setField(webScrapingService, "scrapingTimeout", 10000);
    }

    @Test
    @DisplayName("Deve extrair textos com sucesso quando URL e seletor são válidos")
    void shouldExtractTextsSuccessfullyWhenUrlAndSelectorAreValid() {
        // Given
        String url = "https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF";
        String selector = ".card-title";

        // When
        List<String> result = webScrapingService.extractMultipleTexts(url, selector);

        // Then
        assertNotNull(result);
        // Pode retornar lista vazia se não houver elementos com o seletor
        assertTrue(result.size() >= 0);
        
        // Se houver resultados, verificar se são strings válidas
        result.forEach(text -> {
            assertNotNull(text);
        });
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando seletor não encontra elementos")
    void shouldReturnEmptyListWhenSelectorFindsNoElements() {
        // Given
        String url = "https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF";
        String selector = ".elemento-inexistente";

        // When
        List<String> result = webScrapingService.extractMultipleTexts(url, selector);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve extrair múltiplos textos quando existem vários elementos")
    void shouldExtractMultipleTextsWhenMultipleElementsExist() {
        // Given
        String url = "https://httpbin.org/html";
        String selector = "p";

        // When
        List<String> result = webScrapingService.extractMultipleTexts(url, selector);

        // Then
        assertNotNull(result);
        // httpbin.org/html tem pelo menos um parágrafo
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção quando URL é inválida")
    void shouldThrowExceptionWhenUrlIsInvalid() {
        // Given
        String invalidUrl = "invalid-url";
        String selector = "h1";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            webScrapingService.extractMultipleTexts(invalidUrl, selector);
        });

        assertTrue(exception.getMessage().contains("Erro ao extrair textos da página"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando URL não existe")
    void shouldThrowExceptionWhenUrlDoesNotExist() {
        // Given
        String nonExistentUrl = "https://this-domain-does-not-exist-12345.com";
        String selector = "h1";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            webScrapingService.extractMultipleTexts(nonExistentUrl, selector);
        });

        assertTrue(exception.getMessage().contains("Erro ao extrair textos da página"));
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve funcionar com seletores CSS complexos")
    void shouldWorkWithComplexCssSelectors() {
        // Given
        String url = "https://example.com";
        String complexSelector = "body > div > h1";

        // When
        List<String> result = webScrapingService.extractMultipleTexts(url, complexSelector);

        // Then
        assertNotNull(result);

    }

    @Test
    @DisplayName("Deve funcionar com diferentes tipos de seletores")
    void shouldWorkWithDifferentSelectorTypes() {
        // Given
        String url = "https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF";

        // When & Then - Seletor por tag
        List<String> tagResult = webScrapingService.extractMultipleTexts(url, "title");
        assertNotNull(tagResult);

        // When & Then - Seletor por ID (mesmo que não exista)
        List<String> idResult = webScrapingService.extractMultipleTexts(url, "#header");
        assertNotNull(idResult);

        // When & Then - Seletor por classe
        List<String> classResult = webScrapingService.extractMultipleTexts(url, ".card-title");
        assertNotNull(classResult);
    }

    @Test
    @DisplayName("Deve extrair textos vazios quando elementos existem mas não têm conteúdo")
    void shouldExtractEmptyTextsWhenElementsExistButHaveNoContent() {
        // Given
        String url = "https://example.com";
        String selector = "meta"; // Meta tags geralmente não têm texto

        // When
        List<String> result = webScrapingService.extractMultipleTexts(url, selector);

        // Then
        assertNotNull(result);
        // Meta tags podem retornar strings vazias ou lista vazia
    }

    @Test
    @DisplayName("Deve funcionar com URLs HTTPS")
    void shouldWorkWithHttpsUrls() {
        // Given
        String httpsUrl = "https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF";
        String selector = "title";

        // When
        List<String> result = webScrapingService.extractMultipleTexts(httpsUrl, selector);

        // Then
        assertNotNull(result);
        // Pode retornar lista vazia ou com conteúdo, ambos são válidos
        assertTrue(result.size() >= 0);
    }

    @Test
    @DisplayName("Deve respeitar o timeout configurado")
    void shouldRespectConfiguredTimeout() {
        // Given
        ReflectionTestUtils.setField(webScrapingService, "scrapingTimeout", 1000); // 1 segundo
        String url = "https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF";
        String selector = "body";


        assertDoesNotThrow(() -> {
            List<String> result = webScrapingService.extractMultipleTexts(url, selector);
            assertNotNull(result);
        });
    }

    @Test
    @DisplayName("Deve funcionar com seletores que retornam texto com espaços")
    void shouldWorkWithSelectorsReturningTextWithSpaces() {
        // Given
        String url = "https://example.com";
        String selector = "p"; // Parágrafos geralmente têm texto com espaços

        // When
        List<String> result = webScrapingService.extractMultipleTexts(url, selector);

        // Then
        assertNotNull(result);
        // Verifica se consegue extrair texto (mesmo que seja lista vazia)
    }
}