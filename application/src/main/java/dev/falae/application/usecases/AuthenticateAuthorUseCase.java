package dev.falae.application.usecases;

import dev.falae.application.exceptions.AuthenticationException;
import dev.falae.application.ports.dto.LoginRequest;
import dev.falae.application.ports.dto.LoginResponse;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.services.PasswordService;
import dev.falae.application.ports.services.TokenService;
import dev.falae.core.domain.entities.Author;

public class AuthenticateAuthorUseCase {

    private final AuthorRepository authorRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public AuthenticateAuthorUseCase(AuthorRepository authorRepository,
                                      PasswordService passwordService,
                                      TokenService tokenService) {
        this.authorRepository = authorRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest request) {
        Author author = authorRepository.findByEmail(request.email());

        if (!passwordService.matches(request.password(), author.getPassword())) {
            throw new AuthenticationException();
        }

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
