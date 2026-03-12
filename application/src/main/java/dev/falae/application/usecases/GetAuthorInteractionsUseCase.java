package dev.falae.application.usecases;

import dev.falae.application.ports.dto.AuthorInteractionPageResponse;
import dev.falae.application.ports.repositories.AuthorInteractionRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Author;

public class GetAuthorInteractionsUseCase {

    private final AuthorInteractionRepository authorInteractionRepository;
    private final AuthenticationService authenticationService;

    public GetAuthorInteractionsUseCase(AuthorInteractionRepository authorInteractionRepository,
                                         AuthenticationService authenticationService) {
        this.authorInteractionRepository = authorInteractionRepository;
        this.authenticationService = authenticationService;
    }

    public AuthorInteractionPageResponse execute(int page, int size) {
        Author currentAuthor = authenticationService.getCurrentAuthor();
        return authorInteractionRepository.findByRecipientAuthorId(currentAuthor.getId(), page, size);
    }
}
