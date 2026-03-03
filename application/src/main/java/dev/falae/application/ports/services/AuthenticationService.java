package dev.falae.application.ports.services;

import dev.falae.core.domain.entities.Author;

public interface AuthenticationService {
    Author getCurrentAuthor();
}
