package dev.falae.application.usecases;

import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.CommentPageResponse;
import dev.falae.application.ports.repositories.CommentRepository;

public class GetAuthorCommentsUseCase {

    private final CommentRepository commentRepository;

    public GetAuthorCommentsUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentPageResponse execute(String userName, int page, int size, AuthorContentSortType sortType) {
        return commentRepository.findByAuthorUserName(userName, page, size, sortType);
    }
}