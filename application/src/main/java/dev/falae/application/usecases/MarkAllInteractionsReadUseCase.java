package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.AuthorInteractionRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Author;

public class MarkAllInteractionsReadUseCase {

    private final AuthorInteractionRepository authorInteractionRepository;
    private final AuthenticationService authenticationService;

    public MarkAllInteractionsReadUseCase(AuthorInteractionRepository authorInteractionRepository,
                                           AuthenticationService authenticationService) {
        this.authorInteractionRepository = authorInteractionRepository;
        this.authenticationService = authenticationService;
    }

    public void execute() {
        Author currentAuthor = authenticationService.getCurrentAuthor();
        authorInteractionRepository.markAllAsRead(currentAuthor.getId());
    }
}
