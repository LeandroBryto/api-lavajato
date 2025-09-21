package com.screenshot.service;

import com.screenshot.dto.PncpEditalResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service responsável pelo envio de e-mails
 * Utiliza JavaMailSender do Spring Boot para enviar e-mails em texto simples e HTML
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envia um relatório de scraping por e-mail
     * Suporta tanto texto simples quanto HTML
     * 
     * @param to Destinatário do e-mail
     * @param subject Assunto do e-mail
     * @param content Conteúdo do e-mail (pode conter HTML)
     * @throws RuntimeException se houver erro no envio
     */
    public void sendScrapingReport(String to, String subject, String content) {
        try {
            // Detecta se o conteúdo contém HTML
            boolean isHtml = content.contains("<html>") || content.contains("<div>") || 
                           content.contains("<p>") || content.contains("<br>") || 
                           content.contains("<table>");

            if (isHtml) {
                sendHtmlEmail(to, subject, content);
            } else {
                sendSimpleEmail(to, subject, content);
            }

            logger.info("E-mail de relatório enviado com sucesso para: {}", to);

        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail para {}: {}", to, e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    /**
     * Envia e-mail em texto simples
     */
    private void sendSimpleEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        
        mailSender.send(message);
    }

    /**
     * Envia e-mail em formato HTML
     */
    private void sendHtmlEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // true indica que é HTML
        
        mailSender.send(mimeMessage);
    }



    /**
     * Constrói o conteúdo HTML do e-mail com a lista de editais
     * 
     * @param editais Lista de editais PNCP
     * @return Conteúdo HTML formatado
     */
    public String buildEmailContent(List<PncpEditalResponse> editais) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<html><head><meta charset='UTF-8'></head><body>");
        contentBuilder.append("<p style='font-size: 14px; font-family: Arial, sans-serif;'>");
        contentBuilder.append("Bom dia Memoráveis!<br><br>");
        contentBuilder.append("Hoje temos os seguintes editais encontrados:</p>");

        if (editais == null || editais.isEmpty()) {
            contentBuilder.append("<p style='color: #e74c3c; font-weight: bold;'>");
            contentBuilder.append("Nenhum edital encontrado para os critérios da pesquisa.</p>");
        } else {
            for (PncpEditalResponse edital : editais) {
                contentBuilder.append("<div style='border: 1px solid #ddd; ")
                        .append("border-radius: 6px; padding: 10px; margin: 10px 0; ")
                        .append("background-color: #f9f9f9; font-family: Arial, sans-serif;'>");

                contentBuilder.append("<p><strong>Número do Edital:</strong> ")
                        .append(edital.getNumeroEdital()).append("</p>");

                contentBuilder.append("<p><strong>Órgão:</strong> ")
                        .append(edital.getOrgao()).append("</p>");

                contentBuilder.append("<p><strong>Objeto:</strong> ")
                        .append(edital.getObjeto()).append("</p>");

                contentBuilder.append("<p><strong>Local:</strong> ")
                        .append(edital.getLocal()).append("</p>");

                contentBuilder.append("<p><strong>Modalidade:</strong> ")
                        .append(edital.getModalidade()).append("</p>");

                contentBuilder.append("<p><strong>Última Atualização:</strong> ")
                        .append(edital.getUltimaAtualizacao()).append("</p>");

                contentBuilder.append("</div>");
            }
        }

        contentBuilder.append("<br><p style='font-size: 13px; color: #555;'>");
        contentBuilder.append("Memora – Processos Inovadores</p>");
        contentBuilder.append("</body></html>");

        return contentBuilder.toString();
    }


}