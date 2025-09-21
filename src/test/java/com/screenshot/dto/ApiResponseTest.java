package com.screenshot.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiResponse DTO Tests")
class ApiResponseTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("ApiResponse com construtor completo")
    void shouldCreateApiResponseWithAllArgsConstructor() {
        // Given
        boolean success = true;
        String message = "Test message";
        String data = "Test data";
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        ApiResponse<String> response = new ApiResponse<>(success, message, data, timestamp);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    @DisplayName("Deve criar ApiResponse vazio com construtor padrão")
    void shouldCreateEmptyApiResponseWithNoArgsConstructor() {
        // When
        ApiResponse<String> response = new ApiResponse<>();

        // Then
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
        assertNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Deve criar ApiResponse com construtor personalizado e timestamp automático")
    void shouldCreateApiResponseWithCustomConstructorAndAutoTimestamp() {
        // Given
        boolean success = true;
        String message = "Test message";
        String data = "Test data";
        LocalDateTime before = LocalDateTime.now();

        // When
        ApiResponse<String> response = new ApiResponse<>(success, message, data);

        // Then
        LocalDateTime after = LocalDateTime.now();
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(response.getTimestamp().isBefore(after.plusSeconds(1)));
    }

    @Test
    @DisplayName("Deve criar resposta de sucesso com método estático success(data)")
    void shouldCreateSuccessResponseWithStaticMethodSuccessData() {
        // Given
        List<String> data = Arrays.asList("item1", "item2");

        // When
        ApiResponse<List<String>> response = ApiResponse.success(data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Operação realizada com sucesso", response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Deve criar resposta de sucesso com método estático success(message, data)")
    void shouldCreateSuccessResponseWithStaticMethodSuccessMessageData() {
        // Given
        String message = "Custom success message";
        String data = "Custom data";

        // When
        ApiResponse<String> response = ApiResponse.success(message, data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Deve criar resposta de erro com método estático error(message)")
    void shouldCreateErrorResponseWithStaticMethodError() {
        // Given
        String errorMessage = "Something went wrong";

        // When
        ApiResponse<String> response = ApiResponse.error(errorMessage);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Deve funcionar corretamente com setters do Lombok")
    void shouldWorkCorrectlyWithLombokSetters() {
        // Given
        ApiResponse<String> response = new ApiResponse<>();
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        response.setSuccess(true);
        response.setMessage("Updated message");
        response.setData("Updated data");
        response.setTimestamp(timestamp);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Updated message", response.getMessage());
        assertEquals("Updated data", response.getData());
        assertEquals(timestamp, response.getTimestamp());
    }

    @Test
    @DisplayName("Deve gerar toString corretamente com Lombok")
    void shouldGenerateToStringCorrectlyWithLombok() {
        // Given
        ApiResponse<String> response = ApiResponse.success("Test data");

        // When
        String toString = response.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("ApiResponse"));
        assertTrue(toString.contains("true"));
        assertTrue(toString.contains("Test data"));
    }

    @Test
    @DisplayName("Deve implementar equals e hashCode corretamente com Lombok")
    void shouldImplementEqualsAndHashCodeCorrectlyWithLombok() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        ApiResponse<String> response1 = new ApiResponse<>(true, "message", "data", timestamp);
        ApiResponse<String> response2 = new ApiResponse<>(true, "message", "data", timestamp);
        ApiResponse<String> response3 = new ApiResponse<>(false, "message", "data", timestamp);

        // Then
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    @DisplayName("Deve serializar para JSON corretamente")
    void shouldSerializeToJsonCorrectly() throws Exception {
        // Given
        ApiResponse<String> response = ApiResponse.success("Test data");

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("\"message\":\"Operação realizada com sucesso\""));
        assertTrue(json.contains("\"data\":\"Test data\""));
        assertTrue(json.contains("\"timestamp\":"));
    }

    @Test
    @DisplayName("Deve deserializar de JSON corretamente")
    void shouldDeserializeFromJsonCorrectly() throws Exception {
        // Given
        String json = "{\"success\":true,\"message\":\"Test message\",\"data\":\"Test data\",\"timestamp\":\"2023-01-01 12:00:00\"}";

        // When
        ApiResponse<String> response = objectMapper.readValue(json, ApiResponse.class);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals("Test data", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Deve trabalhar com diferentes tipos de dados genéricos")
    void shouldWorkWithDifferentGenericDataTypes() {
        // Given & When
        ApiResponse<Integer> intResponse = ApiResponse.success(42);
        ApiResponse<List<String>> listResponse = ApiResponse.success(Arrays.asList("a", "b", "c"));
        ApiResponse<Boolean> boolResponse = ApiResponse.error("Error message");

        // Then
        assertEquals(Integer.valueOf(42), intResponse.getData());
        assertEquals(3, listResponse.getData().size());
        assertNull(boolResponse.getData());
        assertFalse(boolResponse.isSuccess());
    }
}