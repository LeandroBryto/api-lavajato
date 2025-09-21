package com.screenshot.controller;

import com.screenshot.dto.EmailRequest;
import com.screenshot.dto.PncpEditalResponse;
import com.screenshot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EnvioEmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmailWithEditais(@RequestBody EmailRequest request) {
        try {
            // Usar o método buildEmailContent do EmailService
            String emailContent = emailService.buildEmailContent(request.getEditais());
            
            // Enviar o e-mail usando o EmailService
            emailService.sendScrapingReport(request.getTo(), request.getSubject(), emailContent);
            
            return ResponseEntity.ok("E-mail enviado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}