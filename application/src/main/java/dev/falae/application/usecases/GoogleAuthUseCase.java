package dev.falae.application.usecases;

import dev.falae.application.exceptions.AuthenticationException;
import dev.falae.application.exceptions.BusinessRuleException;
import dev.falae.application.ports.dto.GoogleAuthRequest;
import dev.falae.application.ports.dto.GoogleUserInfo;
import dev.falae.application.ports.dto.LoginResponse;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.services.GoogleTokenVerificationService;
import dev.falae.application.ports.services.SendEmailService;
import dev.falae.application.ports.services.TokenService;
import dev.falae.core.domain.entities.Author;

import java.util.Optional;
import java.util.UUID;

public class GoogleAuthUseCase {

    private final AuthorRepository authorRepository;
    private final GoogleTokenVerificationService googleTokenVerificationService;
    private final TokenService tokenService;
    private final SendEmailService sendEmailService;

    public GoogleAuthUseCase(AuthorRepository authorRepository,
                             GoogleTokenVerificationService googleTokenVerificationService,
                             TokenService tokenService, SendEmailService sendEmailService) {
        this.authorRepository = authorRepository;
        this.googleTokenVerificationService = googleTokenVerificationService;
        this.tokenService = tokenService;
        this.sendEmailService = sendEmailService;
    }

    public LoginResponse authenticate(GoogleAuthRequest request) {
        GoogleUserInfo googleUser = googleTokenVerificationService.verifyToken(request.credential());

        if (!googleUser.emailVerified()) {
            throw new AuthenticationException("Google email not verified");
        }

        Optional<Author> existingAuthor = authorRepository.findByEmailOptional(googleUser.email());

        if (existingAuthor.isPresent()) {
            Author author = existingAuthor.get();

            if (!author.isGoogleLogin()) {
                throw new BusinessRuleException("This email is already registered with password login. Please use email/password to sign in.");
            }

            return createLoginResponse(author);
        }

        Author newAuthor = createGoogleAuthor(googleUser);
        Author savedAuthor = authorRepository.save(newAuthor);
        sendEmailService.sendWelcomeEmail(savedAuthor.getEmail(), savedAuthor.getName());
        return createLoginResponse(savedAuthor);
    }

    private Author createGoogleAuthor(GoogleUserInfo googleUser) {
        String userName = generateUserName(googleUser.email());

        return new Author(
                UUID.randomUUID(),
                googleUser.email(),
                null,
                googleUser.name(),
                userName,
                null,
                null,
                null,
                0,
                true,
                null,
                true
        );
    }

    private String generateUserName(String email) {
        String baseUserName = email.split("@")[0]
                .toLowerCase()
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (baseUserName.length() > 25) {
            baseUserName = baseUserName.substring(0, 25);
        }

        String userName = baseUserName;
        int suffix = 1;

        while (authorRepository.existsByUserName(userName)) {
            userName = baseUserName + "-" + suffix;
            suffix++;
        }

        return userName;
    }

    private LoginResponse createLoginResponse(Author author) {
        String token = tokenService.generateToken(author.getId(), author.getEmail(), author.getRole());

        return new LoginResponse(
                "Login successful",
                author.getRole(),
                author.getEmail(),
                author.getUserName(),
                author.getProfileImageUrl(),
                author.getName(),
                token,
                author.isEmailVerified()
        );
    }
}
