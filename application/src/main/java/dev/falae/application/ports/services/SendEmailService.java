package dev.falae.application.ports.services;

import java.util.concurrent.CompletableFuture;

public interface SendEmailService {
    CompletableFuture<Void> sendWelcomeEmail(String email, String name);
    CompletableFuture<Void> sendVerificationEmail(String email, String name, String verificationToken);
}
