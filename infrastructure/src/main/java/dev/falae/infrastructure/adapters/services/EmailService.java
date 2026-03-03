package dev.falae.infrastructure.adapters.services;

import dev.falae.application.ports.services.SendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService implements SendEmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final RestTemplate restTemplate;

    @Value("${zeptomail.api-url}")
    private String apiUrl;

    @Value("${zeptomail.api-key}")
    private String apiKey;

    @Value("${zeptomail.from-address}")
    private String fromAddress;

    @Value("${zeptomail.from-name}")
    private String fromName;

    @Value("${app.frontend-url:https://falae.dev}")
    private String frontendUrl;

    public EmailService() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CompletableFuture<Void> sendWelcomeEmail(String email, String name) {
        return CompletableFuture.runAsync(() -> {
            try {
                String template = loadTemplateEmail();
                String htmlContent = template.replace("#{nome}", name);

                sendEmail(email, name, "Bem-vindo ao Falae.dev", htmlContent);
                logger.info("Welcome email sent successfully to: {}", email);
            } catch (Exception e) {
                logger.error("Failed to send welcome email to: {}", email, e);
                throw new RuntimeException("Failed to send email", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> sendVerificationEmail(String email, String name, String verificationToken) {
        return CompletableFuture.runAsync(() -> {
            try {
                String template = loadVerificationTemplateEmail();
                String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;
                String htmlContent = template
                        .replace("#{nome}", name)
                        .replace("#{verificationLink}", verificationLink);

                sendEmail(email, name, "Verifique seu email - Falae.dev", htmlContent);
                logger.info("Verification email sent successfully to: {}", email);
            } catch (Exception e) {
                logger.error("Failed to send verification email to: {}", email, e);
                throw new RuntimeException("Failed to send verification email", e);
            }
        });
    }

    private void sendEmail(String toEmail, String toName, String subject, String htmlBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("Authorization", apiKey);

        Map<String, Object> payload = Map.of(
                "from", Map.of("address", fromAddress),
                "to", List.of(
                        Map.of("email_address", Map.of(
                                "address", toEmail,
                                "name", toName
                        ))
                ),
                "subject", subject,
                "htmlbody", htmlBody
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        logger.info("Sending email to: {} via ZeptoMail API", toEmail);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
        logger.info("ZeptoMail API response: {}", response.getBody());
    }

    public String loadTemplateEmail() throws IOException {
        try (var inputStream = getClass().getResourceAsStream("/template-email.html")) {
            if (inputStream == null) {
                throw new IOException("Template não encontrado: /template-email.html");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String loadVerificationTemplateEmail() throws IOException {
        try (var inputStream = getClass().getResourceAsStream("/template-email-verification.html")) {
            if (inputStream == null) {
                throw new IOException("Template não encontrado: /template-email-verification.html");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
