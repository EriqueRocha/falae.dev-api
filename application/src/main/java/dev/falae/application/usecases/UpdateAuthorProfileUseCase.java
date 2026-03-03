package dev.falae.application.usecases;

import dev.falae.application.exceptions.ResourceAlreadyExistsException;
import dev.falae.application.ports.dto.UpdateAuthorProfileRequest;
import dev.falae.application.ports.dto.UpdateAuthorProfileResponse;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.application.ports.services.PasswordService;
import dev.falae.core.domain.entities.Author;

public class UpdateAuthorProfileUseCase {

    private final AuthorRepository authorRepository;
    private final AuthenticationService authenticationService;
    private final PasswordService passwordService;

    public UpdateAuthorProfileUseCase(AuthorRepository authorRepository,
                                      AuthenticationService authenticationService,
                                      PasswordService passwordService) {
        this.authorRepository = authorRepository;
        this.authenticationService = authenticationService;
        this.passwordService = passwordService;
    }

    public UpdateAuthorProfileResponse execute(UpdateAuthorProfileRequest request) {
        Author author = authenticationService.getCurrentAuthor();

        if (request.email() != null && !request.email().isBlank()) {
            if (authorRepository.existsByEmailAndIdNot(request.email(), author.getId())) {
                throw new ResourceAlreadyExistsException("Author", "email", request.email());
            }
            author.setEmail(request.email());
        }

        if (request.userName() != null && !request.userName().isBlank()) {
            if (authorRepository.existsByUserNameAndIdNot(request.userName(), author.getId())) {
                throw new ResourceAlreadyExistsException("Author", "userName", request.userName());
            }
            author.setUserName(request.userName());
        }

        if (request.password() != null && !request.password().isBlank()) {
            author.setPassword(passwordService.encode(request.password()));
        }

        if (request.name() != null) {
            author.setName(request.name());
        }

        if (request.gitHub() != null) {
            author.setGitHub(request.gitHub());
        }

        if (request.profileImageUrl() != null) {
            author.setProfileImageUrl(request.profileImageUrl());
        }

        if (request.bio() != null) {
            author.setBio(request.bio());
        }

        Author updatedAuthor = authorRepository.save(author);

        return new UpdateAuthorProfileResponse(
                "Perfil atualizado com sucesso",
                updatedAuthor.getEmail(),
                updatedAuthor.getName(),
                updatedAuthor.getUserName(),
                updatedAuthor.getGitHub(),
                updatedAuthor.getProfileImageUrl(),
                updatedAuthor.getBio()
        );
    }
}
