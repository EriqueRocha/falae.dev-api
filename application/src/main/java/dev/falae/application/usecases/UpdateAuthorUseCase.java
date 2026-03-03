package dev.falae.application.usecases;

import dev.falae.application.ports.dto.UpdateAuthorRequest;
import dev.falae.application.ports.dto.UpdateAuthorResponse;
import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Author;

public class UpdateAuthorUseCase {

    private final AuthorRepository authorRepository;
    private final AuthenticationService authenticationService;

    public UpdateAuthorUseCase(AuthorRepository authorRepository, AuthenticationService authenticationService) {
        this.authorRepository = authorRepository;
        this.authenticationService = authenticationService;
    }

    public UpdateAuthorResponse update(UpdateAuthorRequest request) {
        Author author = authenticationService.getCurrentAuthor();
        author.setName(request.name());
        author.setGitHub(request.gitHub());
        author.setBio(request.bio());

        Author updatedAuthor = authorRepository.save(author);

        return new UpdateAuthorResponse(
                "Author updated successfully",
                updatedAuthor.getName(),
                updatedAuthor.getGitHub(),
                updatedAuthor.getBio()
        );
    }
}
