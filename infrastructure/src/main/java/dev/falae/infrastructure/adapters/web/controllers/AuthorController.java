package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.*;
import dev.falae.application.usecases.*;
import dev.falae.infrastructure.adapters.services.AuthorProfileImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final CreateAuthorUseCase createAuthorUseCase;
    private final UpdateAuthorUseCase updateAuthorUseCase;
    private final UpdateAuthorProfileUseCase updateAuthorProfileUseCase;
    private final GetAuthorProfileUseCase getAuthorProfileUseCase;
    private final GetAuthorArticlesUseCase getAuthorArticlesUseCase;
    private final GetAuthorTopicsUseCase getAuthorTopicsUseCase;
    private final GetAuthorCommentsUseCase getAuthorCommentsUseCase;
    private final AuthorProfileImageService authorProfileImageService;

    public AuthorController(CreateAuthorUseCase createAuthorUseCase,
                            UpdateAuthorUseCase updateAuthorUseCase,
                            UpdateAuthorProfileUseCase updateAuthorProfileUseCase,
                            GetAuthorProfileUseCase getAuthorProfileUseCase,
                            GetAuthorArticlesUseCase getAuthorArticlesUseCase,
                            GetAuthorTopicsUseCase getAuthorTopicsUseCase,
                            GetAuthorCommentsUseCase getAuthorCommentsUseCase,
                            AuthorProfileImageService authorProfileImageService) {
        this.createAuthorUseCase = createAuthorUseCase;
        this.updateAuthorUseCase = updateAuthorUseCase;
        this.updateAuthorProfileUseCase = updateAuthorProfileUseCase;
        this.getAuthorProfileUseCase = getAuthorProfileUseCase;
        this.getAuthorArticlesUseCase = getAuthorArticlesUseCase;
        this.getAuthorTopicsUseCase = getAuthorTopicsUseCase;
        this.getAuthorCommentsUseCase = getAuthorCommentsUseCase;
        this.authorProfileImageService = authorProfileImageService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateAuthorResponse> createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createAuthorUseCase.create(request));
    }

    @PatchMapping("/basic-profile-update")
    public ResponseEntity<UpdateAuthorResponse> authorBasicUpdate(@Valid @RequestBody UpdateAuthorRequest request) {
        return ResponseEntity.ok(updateAuthorUseCase.update(request));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UpdateAuthorProfileResponse> updateProfile(@Valid @RequestBody UpdateAuthorProfileRequest request) {
        return ResponseEntity.ok(updateAuthorProfileUseCase.execute(request));
    }

    @PostMapping(value = "/profile-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Envia a foto de perfil do autor (author) extensão JPG/JPEG, PNG, GIF, WEBP - máximo 600KB")
    public ResponseEntity<String> uploadProfileImage(@RequestPart("file") MultipartFile file) {
        String imageUrl = authorProfileImageService.uploadProfileImage(file);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/{userName}")
    public ResponseEntity<AuthorProfileResponse> getAuthorProfile(@PathVariable String userName) {
        return ResponseEntity.ok(getAuthorProfileUseCase.execute(userName));
    }

    @GetMapping("/{userName}/articles")
    public ResponseEntity<ArticlePageResponse> getAuthorArticles(
            @PathVariable String userName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RECENT") AuthorContentSortType sort) {
        return ResponseEntity.ok(getAuthorArticlesUseCase.execute(userName, page, size, sort));
    }

    @GetMapping("/{userName}/topics")
    public ResponseEntity<TopicPageResponse> getAuthorTopics(
            @PathVariable String userName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RECENT") AuthorContentSortType sort) {
        return ResponseEntity.ok(getAuthorTopicsUseCase.execute(userName, page, size, sort));
    }

    @GetMapping("/{userName}/comments")
    public ResponseEntity<CommentPageResponse> getAuthorComments(
            @PathVariable String userName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RECENT") AuthorContentSortType sort) {
        return ResponseEntity.ok(getAuthorCommentsUseCase.execute(userName, page, size, sort));
    }
}
