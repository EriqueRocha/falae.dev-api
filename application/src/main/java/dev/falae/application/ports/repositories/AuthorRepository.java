package dev.falae.application.ports.repositories;

import dev.falae.application.ports.dto.AuthorProfileResponse;
import dev.falae.core.domain.entities.Author;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository {
    Author findByEmail(String email);
    Optional<Author> findByEmailOptional(String email);
    Author findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmailAndIdNot(String email, UUID id);
    boolean existsByUserNameAndIdNot(String userName, UUID id);
    AuthorProfileResponse findProfileByUserName(String userName);
    Author save(Author author);
    Author findById(UUID id);
    void deleteById(UUID id);
    void addCoinsToCurrentAuthor(int coins);
}