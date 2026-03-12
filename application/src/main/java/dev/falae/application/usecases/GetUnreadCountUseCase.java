package dev.falae.application.usecases;

import dev.falae.application.ports.dto.UnreadCountResponse;
import dev.falae.application.ports.repositories.AuthorInteractionRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Author;

public class GetUnreadCountUseCase {

    private final AuthorInteractionRepository authorInteractionRepository;
    private final AuthenticationService authenticationService;

    public GetUnreadCountUseCase(AuthorInteractionRepository authorInteractionRepository,
                                  AuthenticationService authenticationService) {
        this.authorInteractionRepository = authorInteractionRepository;
        this.authenticationService = authenticationService;
    }

    public UnreadCountResponse execute() {
        Author currentAuthor = authenticationService.getCurrentAuthor();
        long count = authorInteractionRepository.countUnreadByRecipientAuthorId(currentAuthor.getId());
        return new UnreadCountResponse(count);
    }
}
