package dev.falae.infrastructure.config.security;

import dev.falae.application.ports.repositories.AuthorRepository;
import dev.falae.application.ports.services.AuthenticationService;
import dev.falae.core.domain.entities.Author;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService implements AuthenticationService {

    private final AuthenticatedAuthorProvider authenticatedAuthorProvider;
    private final AuthorRepository authorRepository;

    public AuthenticatedUserService(AuthenticatedAuthorProvider authenticatedAuthorProvider, AuthorRepository authorRepository) {
        this.authenticatedAuthorProvider = authenticatedAuthorProvider;
        this.authorRepository = authorRepository;
    }

    @Override
    public Author getCurrentAuthor(){
        AuthorEntity authorEntity = authenticatedAuthorProvider.getCurrentAuthor();
        return authorRepository.findById(authorEntity.getId());
    }
}
