package dev.falae.application.usecases;

import dev.falae.application.ports.repositories.AuthorRepository;

import java.util.UUID;

public class DeleteAuthorUseCase {

    private final AuthorRepository authorRepository;

    public DeleteAuthorUseCase(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public void delete(UUID authorId) {
        authorRepository.deleteById(authorId);
    }

}