package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.AuthorInteractionRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Author;

import java.util.UUID;

public class MarkInteractionReadUseCase {

    private final AuthorInteractionRepository authorInteractionRepository;
    private final AuthenticationService authenticationService;

    public MarkInteractionReadUseCase(AuthorInteractionRepository authorInteractionRepository,
                                       AuthenticationService authenticationService) {
        this.authorInteractionRepository = authorInteractionRepository;
        this.authenticationService = authenticationService;
    }

    public void execute(UUID interactionId) {
        Author currentAuthor = authenticationService.getCurrentAuthor();
        authorInteractionRepository.markAsRead(interactionId, currentAuthor.getId());
    }
}
