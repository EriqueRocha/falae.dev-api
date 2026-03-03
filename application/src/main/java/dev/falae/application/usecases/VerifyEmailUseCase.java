package dev.falae.application.usecases;

import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.exceptions.ValidationException;
import dev.falae.application.ports.dto.VerifyEmailResponse;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.services.TokenService;
import dev.falae.core.domain.entities.Author;

import java.util.UUID;

public class VerifyEmailUseCase {

    private final AuthorRepository authorRepository;
    private final TokenService tokenService;

    public VerifyEmailUseCase(AuthorRepository authorRepository, TokenService tokenService) {
        this.authorRepository = authorRepository;
        this.tokenService = tokenService;
    }

    public VerifyEmailResponse verify(String verificationToken) {
        if (!tokenService.validateEmailVerificationToken(verificationToken)) {
            throw new ValidationException("Invalid or expired verification token");
        }

        UUID userId = tokenService.extractUserIdFromVerificationToken(verificationToken);
        Author author = authorRepository.findById(userId);

        if (author.isEmailVerified()) {
            throw new BusinessRuleException("Email is already verified");
        }

        author.setEmailVerified(true);
        authorRepository.save(author);

        String loginToken = tokenService.generateToken(author.getId(), author.getEmail(), author.getRole());

        return new VerifyEmailResponse(
                "Email verified successfully",
                author.getUserName(),
                loginToken
        );
    }
}
