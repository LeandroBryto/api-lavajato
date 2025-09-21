package com.screenshot.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ScrapingRequest DTO Tests")
class ScrapingRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve criar ScrapingRequest válido com construtor completo")
    void shouldCreateValidScrapingRequestWithAllArgsConstructor() {
        // Given
        String url = "https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF";
        String selector = ".card-title";

        // When
        ScrapingRequest request = new ScrapingRequest(url, selector);

        // Then
        assertEquals(url, request.getUrl());
        assertEquals(selector, request.getSelector());
    }

    @Test
    @DisplayName("Deve criar ScrapingRequest vazio com construtor padrão")
    void shouldCreateEmptyScrapingRequestWithNoArgsConstructor() {
        // When
        ScrapingRequest request = new ScrapingRequest();

        // Then
        assertNull(request.getUrl());
        assertNull(request.getSelector());
    }

    @Test
    @DisplayName("Deve validar ScrapingRequest com dados válidos")
    void shouldValidateValidScrapingRequest() {
        // Given
        ScrapingRequest request = new ScrapingRequest("https://example.com", "h1");

        // When
        Set<ConstraintViolation<ScrapingRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve falhar validação quando URL é null")
    void shouldFailValidationWhenUrlIsNull() {
        // Given
        ScrapingRequest request = new ScrapingRequest(null, ".card-title");

        // When
        Set<ConstraintViolation<ScrapingRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<ScrapingRequest> violation = violations.iterator().next();
        assertEquals("URL é obrigatória", violation.getMessage());
        assertEquals("url", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve falhar validação quando URL está vazia")
    void shouldFailValidationWhenUrlIsBlank() {
        // Given
        ScrapingRequest request = new ScrapingRequest("", "h1");

        // When
        Set<ConstraintViolation<ScrapingRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<ScrapingRequest> violation = violations.iterator().next();
        assertEquals("URL não pode estar vazia", violation.getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação quando seletor é null")
    void shouldFailValidationWhenSelectorIsNull() {
        // Given
        ScrapingRequest request = new ScrapingRequest("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", null);

        // When
        Set<ConstraintViolation<ScrapingRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<ScrapingRequest> violation = violations.iterator().next();
        assertEquals("Seletor é obrigatório", violation.getMessage());
        assertEquals("selector", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve falhar validação quando seletor está vazio")
    void shouldFailValidationWhenSelectorIsBlank() {
        // Given
        ScrapingRequest request = new ScrapingRequest("https://example.com", "");

        // When
        Set<ConstraintViolation<ScrapingRequest>> violations = validator.validate(request);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<ScrapingRequest> violation = violations.iterator().next();
        assertEquals("Seletor não pode estar vazio", violation.getMessage());
    }

    @Test
    @DisplayName("Deve falhar validação quando ambos URL e seletor são inválidos")
    void shouldFailValidationWhenBothUrlAndSelectorAreInvalid() {
        // Given
        ScrapingRequest request = new ScrapingRequest(null, "");

        // When
        Set<ConstraintViolation<ScrapingRequest>> violations = validator.validate(request);

        // Then
        assertEquals(2, violations.size());
    }

    @Test
    @DisplayName("Deve criar ScrapingRequest válido com construtor vazio e setters")
    void shouldCreateValidScrapingRequestWithNoArgsConstructorAndSetters() {
        // Given
        String url = "https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF";
        String selector = ".card-title";

        // When
        ScrapingRequest request = new ScrapingRequest();
        request.setUrl(url);
        request.setSelector(selector);

        // Then
        assertEquals(url, request.getUrl());
        assertEquals(selector, request.getSelector());
    }

    @Test
    @DisplayName("Deve funcionar corretamente com setters do Lombok")
    void shouldWorkCorrectlyWithLombokSetters() {
        // Given
        ScrapingRequest request = new ScrapingRequest();
        String url = "https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF";
        String selector = ".card-title";

        // When
        request.setUrl(url);
        request.setSelector(selector);

        // Then
        assertEquals(url, request.getUrl());
        assertEquals(selector, request.getSelector());
    }

    @Test
    @DisplayName("Deve gerar toString corretamente com Lombok")
    void shouldGenerateToStringCorrectlyWithLombok() {
        // Given
        ScrapingRequest request = new ScrapingRequest("https://example.com", "h1");

        // When
        String toString = request.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("https://example.com"));
        assertTrue(toString.contains("h1"));
        assertTrue(toString.contains("ScrapingRequest"));
    }

    @Test
    @DisplayName("Deve implementar equals e hashCode corretamente com Lombok")
    void shouldImplementEqualsAndHashCodeCorrectlyWithLombok() {
        // Given
        ScrapingRequest request1 = new ScrapingRequest("https://example.com", "h1");
        ScrapingRequest request2 = new ScrapingRequest("https://example.com", "h1");
        ScrapingRequest request3 = new ScrapingRequest("https://different.com", "h1");

        // Then
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }
}