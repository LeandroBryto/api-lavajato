package com.screenshot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrapingRequest {
    
    @NotNull(message = "URL é obrigatória")
    @NotBlank(message = "URL não pode estar vazia")
    private String url;
    
    @NotNull(message = "Seletor é obrigatório")
    @NotBlank(message = "Seletor não pode estar vazio")
    private String selector;
}
