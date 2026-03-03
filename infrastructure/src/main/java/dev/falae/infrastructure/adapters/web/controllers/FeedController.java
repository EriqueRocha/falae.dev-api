package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.FeedItemResponse;
import dev.falae.application.ports.dto.FeedPageResponse;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.usecases.FindFeedUseCase;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final FindFeedUseCase findFeedUseCase;

    public FeedController(FindFeedUseCase findFeedUseCase) {
        this.findFeedUseCase = findFeedUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar feed unificado (artigos, tópicos e comentários)")
    public ResponseEntity<FeedPageResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RECENT") FeedSortType sort,
            @RequestParam(required = false) FeedItemResponse.FeedItemType type) {
        return ResponseEntity.ok(findFeedUseCase.findAll(page, size, sort, type));
    }
}
