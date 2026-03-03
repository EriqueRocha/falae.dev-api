package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.usecases.AdminDeleteArticleUseCase;
import dev.falae.application.usecases.AdminDeleteCommentUseCase;
import dev.falae.application.usecases.AdminDeleteTopicUseCase;
import dev.falae.application.usecases.DeleteAuthorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminDeleteArticleUseCase adminDeleteArticleUseCase;
    private final DeleteAuthorUseCase deleteAuthorUseCase;
    private final AdminDeleteCommentUseCase adminDeleteCommentUseCase;
    private final AdminDeleteTopicUseCase adminDeleteTopicUseCase;

    public AdminController(AdminDeleteArticleUseCase adminDeleteArticleUseCase,
                           DeleteAuthorUseCase deleteAuthorUseCase,
                           AdminDeleteCommentUseCase adminDeleteCommentUseCase,
                           AdminDeleteTopicUseCase adminDeleteTopicUseCase) {
        this.adminDeleteArticleUseCase = adminDeleteArticleUseCase;
        this.deleteAuthorUseCase = deleteAuthorUseCase;
        this.adminDeleteCommentUseCase = adminDeleteCommentUseCase;
        this.adminDeleteTopicUseCase = adminDeleteTopicUseCase;
    }

    @DeleteMapping("/article/{articleId}")
    @Operation(summary = "Deleta um artigo (admin)")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID articleId) {
        adminDeleteArticleUseCase.delete(articleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/author/{authorId}")
    @Operation(summary = "Deleta um autor (admin)")
    public ResponseEntity<Void> deleteAuthor(@PathVariable UUID authorId) {
        deleteAuthorUseCase.delete(authorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "Deleta um comentário (admin)")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        adminDeleteCommentUseCase.delete(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/topic/{topicId}")
    @Operation(summary = "Deleta um tópico (admin)")
    public ResponseEntity<Void> deleteTopic(@PathVariable UUID topicId) {
        adminDeleteTopicUseCase.delete(topicId);
        return ResponseEntity.noContent().build();
    }
}