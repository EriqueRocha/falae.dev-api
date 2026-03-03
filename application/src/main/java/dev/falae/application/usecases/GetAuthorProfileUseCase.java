package dev.falae.application.usecases;

import dev.falae.application.ports.dto.AuthorProfileResponse;
import dev.falae.application.ports.repositories.AuthorRepository;

public class GetAuthorProfileUseCase {

    private final AuthorRepository authorRepository;

    public GetAuthorProfileUseCase(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public AuthorProfileResponse execute(String userName) {
        return authorRepository.findProfileByUserName(userName);
    }
}
