package dev.falae.application.usecases;

import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.services.SendEmailService;
import dev.falae.application.ports.services.TokenService;
import dev.falae.core.domain.entities.Author;

public class ResendVerificationEmailUseCase {

    private final AuthorRepository authorRepository;
    private final TokenService tokenService;
    private final SendEmailService sendEmailService;

    public ResendVerificationEmailUseCase(AuthorRepository authorRepository,
                                          TokenService tokenService,
                                          SendEmailService sendEmailService) {
        this.authorRepository = authorRepository;
        this.tokenService = tokenService;
        this.sendEmailService = sendEmailService;
    }

    public void resend(String email) {
        Author author = authorRepository.findByEmail(email);

        if (author.isEmailVerified()) {
            throw new BusinessRuleException("Email is already verified");
        }

        if (author.isGoogleLogin()) {
            throw new BusinessRuleException("Google accounts are automatically verified");
        }

        String verificationToken = tokenService.generateEmailVerificationToken(author.getId(), author.getEmail());
        sendEmailService.sendVerificationEmail(author.getEmail(), author.getName(), verificationToken);
    }
}
