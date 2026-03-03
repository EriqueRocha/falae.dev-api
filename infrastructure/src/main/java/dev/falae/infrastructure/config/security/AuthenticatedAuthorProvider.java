package dev.falae.infrastructure.config.security;

import dev.falae.application.exceptions.AuthenticationException;
import dev.falae.application.exceptions.ResourceNotFoundException;
import dev.falae.infrastructure.adapters.repositories.entities.AuthorEntity;
import dev.falae.infrastructure.adapters.repositories.jpa.AuthorJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedAuthorProvider {

    private final AuthorJpaRepository authorJpaRepository;

    public AuthenticatedAuthorProvider(AuthorJpaRepository authorJpaRepository) {
        this.authorJpaRepository = authorJpaRepository;
    }

    public AuthorEntity getCurrentAuthor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedAuthor principal)) {
            throw new AuthenticationException("No authenticated author");
        }

        return authorJpaRepository.findById(principal.id())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

    }

    public AuthorEntity getCurrentAuthorOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedAuthor principal)) {
            return null;
        }

        return authorJpaRepository.findById(principal.id()).orElse(null);
    }
}

