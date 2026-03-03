package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.*;
import dev.falae.application.usecases.ArticleInteractionUseCase;
import dev.falae.application.usecases.CreateArticleUseCase;
import dev.falae.application.usecases.DeleteArticleUseCase;
import dev.falae.application.usecases.EditArticleUseCase;
import dev.falae.application.usecases.FindArticleUseCase;
import dev.falae.application.usecases.FindArticlesUseCase;
import dev.falae.infrastructure.adapters.services.ContentArticleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private final CreateArticleUseCase createArticleUseCase;
    private final EditArticleUseCase editArticleUseCase;
    private final ContentArticleService contentArticleService;
    private final DeleteArticleUseCase deleteArticleUseCase;
    private final FindArticleUseCase findArticleUseCase;
    private final FindArticlesUseCase findArticlesUseCase;
    private final ArticleInteractionUseCase articleInteractionUseCase;

    public ArticleController(CreateArticleUseCase createArticleUseCase, EditArticleUseCase editArticleUseCase, ContentArticleService contentArticleService, DeleteArticleUseCase deleteArticleUseCase, FindArticleUseCase findArticleUseCase, FindArticlesUseCase findArticlesUseCase, ArticleInteractionUseCase articleInteractionUseCase) {
        this.createArticleUseCase = createArticleUseCase;
        this.editArticleUseCase = editArticleUseCase;
        this.contentArticleService = contentArticleService;
        this.deleteArticleUseCase = deleteArticleUseCase;
        this.findArticleUseCase = findArticleUseCase;
        this.findArticlesUseCase = findArticlesUseCase;
        this.articleInteractionUseCase = articleInteractionUseCase;
    }

    @PostMapping(value = "/saveNew")
    @Operation(summary = "Cadastrar um artigo (author)")
    public ResponseEntity<CreateArticleResponse> save(@Valid @RequestBody SaveArticleRequest saveArticleRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createArticleUseCase.create(saveArticleRequest));
    }

    @PutMapping(value = "/edit")
    @Operation(summary = "Editar um artigo (author)")
    public ResponseEntity<Void> edit(@Valid @RequestBody EditArticleRequest editArticleRequest) {
        editArticleUseCase.edit(editArticleRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/saveArticleContent", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Envia o arquivo com o conteúdo do artigo (author) extensão .md ou .html")
    public String saveArticleContent(@RequestPart("file") MultipartFile file, UUID articleId) {
        return contentArticleService.linkContentToArticle(file, articleId);
    }

    @PostMapping(value = "/saveArticleCover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Envia a capa do artigo (author) extensão JPG/JPEG, PNG, GIF, WEBP")
    public String saveArticleCover(@RequestPart("file") MultipartFile file, UUID articleId) {
        return contentArticleService.linkCoverToArticle(file, articleId);
    }

    @PostMapping(value = "/saveArticleImage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Envia uma imagem para o artigo (author) extensão JPG/JPEG, PNG, GIF, WEBP")
    public String saveArticleImage(@RequestPart("file") MultipartFile file, UUID articleId) {
        return contentArticleService.linkImageToArticle(file, articleId);
    }

    @GetMapping
    @Operation(summary = "Listar artigos paginados")
    public ResponseEntity<ArticlePageResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RECENT") FeedSortType sort) {
        return ResponseEntity.ok(findArticlesUseCase.findAll(page, size, sort));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar artigos por título")
    public ResponseEntity<ArticlePageResponse> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RECENT") AuthorContentSortType sort) {
        return ResponseEntity.ok(findArticlesUseCase.searchByTitle(title, page, size, sort));
    }

    @GetMapping(value = "/{articleId}")
    @Operation(summary = "Buscar um artigo por ID")
    public ResponseEntity<ArticleResponse> findById(@PathVariable UUID articleId) {
        return ResponseEntity.ok(findArticleUseCase.findById(articleId));
    }

    @GetMapping(value = "/{userName}/{slug}")
    @Operation(summary = "Buscar um artigo por userName do autor e slug")
    public ResponseEntity<ArticleResponse> findByAuthorAndSlug(
            @PathVariable String userName,
            @PathVariable String slug) {
        return ResponseEntity.ok(findArticleUseCase.findByAuthorUserNameAndSlug(userName, slug));
    }

    @DeleteMapping(value = "/delete/{articleId}")
    @Operation(summary = "Deleta um artigo (author)")
    public ResponseEntity<Void> delete(@PathVariable UUID articleId){
        deleteArticleUseCase.delete(articleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{articleId}/like")
    @Operation(summary = "Curtir/descurtir um artigo (author)")
    public ResponseEntity<InteractionResponse> toggleLike(@PathVariable UUID articleId) {
        return ResponseEntity.ok(articleInteractionUseCase.toggleLike(articleId));
    }

    @PostMapping(value = "/{articleId}/save")
    @Operation(summary = "Salvar/remover dos salvos um artigo (author)")
    public ResponseEntity<InteractionResponse> toggleSave(@PathVariable UUID articleId) {
        return ResponseEntity.ok(articleInteractionUseCase.toggleSave(articleId));
    }

    @PostMapping(value = "/{articleId}/dislike")
    @Operation(summary = "Dar/remover dislike em um artigo (author)")
    public ResponseEntity<InteractionResponse> toggleDislike(@PathVariable UUID articleId) {
        return ResponseEntity.ok(articleInteractionUseCase.toggleDislike(articleId));
    }
}
