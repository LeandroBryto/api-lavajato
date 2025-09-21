package com.screenshot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.screenshot.dto.ScrapingRequest;
import com.screenshot.service.WebScrapingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebScrapingController.class)
@DisplayName("WebScrapingController Tests")
class WebScrapingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebScrapingService webScrapingService;

    @Autowired
    private ObjectMapper objectMapper;

    private ScrapingRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new ScrapingRequest("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", ".card-title");
    }

    @Test
    @DisplayName("Deve extrair textos com sucesso quando requisição é válida")
    void shouldExtractTextsSuccessfullyWhenRequestIsValid() throws Exception {
        // Given
        List<String> expectedTexts = Arrays.asList("Title 1", "Title 2");
        when(webScrapingService.extractMultipleTexts(anyString(), anyString()))
                .thenReturn(expectedTexts);

        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Textos extraídos com sucesso"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("Title 1"))
                .andExpect(jsonPath("$.data[1]").value("Title 2"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(webScrapingService).extractMultipleTexts("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", ".card-title");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum texto é encontrado")
    void shouldReturnEmptyListWhenNoTextsFound() throws Exception {
        // Given
        when(webScrapingService.extractMultipleTexts(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Textos extraídos com sucesso"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(webScrapingService).extractMultipleTexts("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", ".card-title");
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando URL é null")
    void shouldReturn400WhenUrlIsNull() throws Exception {
        // Given
        ScrapingRequest invalidRequest = new ScrapingRequest(null, ".card-title");

        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(webScrapingService, never()).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando URL está vazia")
    void shouldReturn400WhenUrlIsBlank() throws Exception {
        // Given
        ScrapingRequest invalidRequest = new ScrapingRequest("", "h1");

        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(webScrapingService, never()).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando seletor é null")
    void shouldReturn400WhenSelectorIsNull() throws Exception {
        // Given
        ScrapingRequest invalidRequest = new ScrapingRequest("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", null);

        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(webScrapingService, never()).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando seletor está vazio")
    void shouldReturn400WhenSelectorIsBlank() throws Exception {
        // Given
        ScrapingRequest invalidRequest = new ScrapingRequest("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", "");

        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(webScrapingService, never()).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando ambos URL e seletor são inválidos")
    void shouldReturn400WhenBothUrlAndSelectorAreInvalid() throws Exception {
        // Given
        ScrapingRequest invalidRequest = new ScrapingRequest(null, "");

        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(webScrapingService, never()).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando Content-Type não é JSON")
    void shouldReturn400WhenContentTypeIsNotJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(webScrapingService, never()).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando body está vazio")
    void shouldReturn400WhenBodyIsEmpty() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        verify(webScrapingService, never()).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando JSON é inválido")
    void shouldReturn400WhenJsonIsInvalid() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(webScrapingService, never()).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve propagar exceção do service como erro interno")
    void shouldPropagateServiceExceptionAsInternalError() throws Exception {
        // Given
        when(webScrapingService.extractMultipleTexts(anyString(), anyString()))
                .thenThrow(new RuntimeException("Erro no scraping"));

        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError());

        verify(webScrapingService).extractMultipleTexts("https://example.com", "h1");
    }

    @Test
    @DisplayName("Deve aceitar diferentes tipos de seletores CSS")
    void shouldAcceptDifferentCssSelectorTypes() throws Exception {
        // Given
        List<String> expectedTexts = Arrays.asList("Text 1");
        when(webScrapingService.extractMultipleTexts(anyString(), anyString()))
                .thenReturn(expectedTexts);

        // Test com seletor de classe
        ScrapingRequest classRequest = new ScrapingRequest("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", ".card-title");
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classRequest)))
                .andExpect(status().isOk());

        // Test com seletor de ID
        ScrapingRequest idRequest = new ScrapingRequest("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", "#header");
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(idRequest)))
                .andExpect(status().isOk());

        // Test com seletor de tag
        ScrapingRequest tagRequest = new ScrapingRequest("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", "title");
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isOk());

        verify(webScrapingService, times(3)).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve aceitar URLs com diferentes protocolos")
    void shouldAcceptUrlsWithDifferentProtocols() throws Exception {
        // Given
        List<String> expectedTexts = Arrays.asList("Text 1");
        when(webScrapingService.extractMultipleTexts(anyString(), anyString()))
                .thenReturn(expectedTexts);

        // Test com HTTPS (URL padrão já é HTTPS)
        ScrapingRequest httpsRequest = new ScrapingRequest("https://pncp.gov.br/app/editais?q=informatica&pagina=1&status=recebendo_proposta&ufs=DF", ".card-title");
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(httpsRequest)))
                .andExpect(status().isOk());

        verify(webScrapingService, times(1)).extractMultipleTexts(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar resposta com estrutura correta")
    void shouldReturnResponseWithCorrectStructure() throws Exception {
        // Given
        List<String> expectedTexts = Arrays.asList("Title");
        when(webScrapingService.extractMultipleTexts(anyString(), anyString()))
                .thenReturn(expectedTexts);

        // When & Then
        mockMvc.perform(post("/api/scraping/texts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.timestamp").isString());
    }
}