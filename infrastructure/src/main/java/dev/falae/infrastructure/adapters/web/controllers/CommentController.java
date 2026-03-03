package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.*;
import dev.falae.application.usecases.CommentInteractionUseCase;
import dev.falae.application.usecases.CreateCommentUseCase;
import dev.falae.application.usecases.DeleteCommentUseCase;
import dev.falae.application.usecases.FindCommentsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CreateCommentUseCase createCommentUseCase;
    private final FindCommentsUseCase findCommentsUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final CommentInteractionUseCase commentInteractionUseCase;

    public CommentController(CreateCommentUseCase createCommentUseCase,
                             FindCommentsUseCase findCommentsUseCase,
                             DeleteCommentUseCase deleteCommentUseCase,
                             CommentInteractionUseCase commentInteractionUseCase) {
        this.createCommentUseCase = createCommentUseCase;
        this.findCommentsUseCase = findCommentsUseCase;
        this.deleteCommentUseCase = deleteCommentUseCase;
        this.commentInteractionUseCase = commentInteractionUseCase;
    }

    @PostMapping("/save")
    @Operation(summary = "Cadastrar um comentário (author)")
    public ResponseEntity<CreateCommentResponse> save(@Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createCommentUseCase.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos os comentários paginados")
    public ResponseEntity<CommentPageResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RECENT") FeedSortType sort) {
        return ResponseEntity.ok(findCommentsUseCase.findAll(page, size, sort));
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Buscar comentários raiz de um artigo (paginado - author)")
    public ResponseEntity<CommentPageResponse> findByArticle(
            @PathVariable UUID articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(findCommentsUseCase.findByArticleId(articleId, page, size));
    }

    @GetMapping("/topic/{topicId}")
    @Operation(summary = "Buscar comentários raiz de um tópico (paginado - author)")
    public ResponseEntity<CommentPageResponse> findByTopic(
            @PathVariable UUID topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(findCommentsUseCase.findByTopicId(topicId, page, size));
    }

    @GetMapping("/{commentId}/replies")
    @Operation(summary = "Buscar respostas diretas de um comentário")
    public ResponseEntity<List<CommentResponse>> findReplies(@PathVariable UUID commentId) {
        return ResponseEntity.ok(findCommentsUseCase.findReplies(commentId));
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Buscar um comentário por ID")
    public ResponseEntity<CommentResponse> findById(@PathVariable UUID commentId) {
        return ResponseEntity.ok(findCommentsUseCase.findById(commentId));
    }

    @DeleteMapping("/delete/{commentId}")
    @Operation(summary = "Deleta um comentário (author)")
    public ResponseEntity<Void> delete(@PathVariable UUID commentId) {
        deleteCommentUseCase.delete(commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like")
    @Operation(summary = "Curtir/descurtir um comentário (author)")
    public ResponseEntity<InteractionResponse> toggleLike(@PathVariable UUID commentId) {
        return ResponseEntity.ok(commentInteractionUseCase.toggleLike(commentId));
    }

    @PostMapping("/{commentId}/dislike")
    @Operation(summary = "Dar/remover dislike em um comentário (author)")
    public ResponseEntity<InteractionResponse> toggleDislike(@PathVariable UUID commentId) {
        return ResponseEntity.ok(commentInteractionUseCase.toggleDislike(commentId));
    }
}
