package dev.falae.application.usecases;

import dev.falae.application.exceptions.ResourceAlreadyExistsException;
import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.application.ports.dto.CreateAuthorRequest;
import dev.falae.application.ports.dto.CreateAuthorResponse;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.services.PasswordService;
import dev.falae.application.ports.services.SendEmailService;
import dev.falae.application.ports.services.TokenService;
import dev.falae.core.domain.entities.Author;

public class CreateAuthorUseCase {

    private final AuthorRepository authorRepository;
    private final PasswordService passwordService;
    private final SendEmailService sendEmailService;
    private final TokenService tokenService;

    public CreateAuthorUseCase(AuthorRepository authorRepository,
                               PasswordService passwordService,
                               SendEmailService sendEmailService,
                               TokenService tokenService) {
        this.authorRepository = authorRepository;
        this.passwordService = passwordService;
        this.sendEmailService = sendEmailService;
        this.tokenService = tokenService;
    }

    public CreateAuthorResponse create(CreateAuthorRequest request) {
        try {
            authorRepository.findByEmail(request.email());
            throw new ResourceAlreadyExistsException("Author", "email", request.email());
        } catch (ResourceNotFoundException e) {
            //email not found, we can proceed
        }

        if (authorRepository.existsByUserName(request.userName())) {
            throw new ResourceAlreadyExistsException("Author", "userName", request.userName());
        }

        String encodedPassword = passwordService.encode(request.password());
        Author newAuthor = new Author(request.email(), encodedPassword, request.name(), request.userName());
        Author savedAuthor = authorRepository.save(newAuthor);

        String verificationToken = tokenService.generateEmailVerificationToken(savedAuthor.getId(), savedAuthor.getEmail());
        sendEmailService.sendVerificationEmail(savedAuthor.getEmail(), savedAuthor.getName(), verificationToken);

        return new CreateAuthorResponse(
                "Author created successfully. Please check your email to verify your account.",
                savedAuthor.getId(),
                savedAuthor.getEmail(),
                savedAuthor.getName()
        );
    }
}
