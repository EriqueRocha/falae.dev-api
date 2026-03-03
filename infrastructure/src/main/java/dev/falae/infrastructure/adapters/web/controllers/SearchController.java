package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.dto.SearchContentType;
import dev.falae.application.ports.dto.TagSearchPageResponse;
import dev.falae.application.usecases.SearchByTagsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Endpoints para busca de conteúdo")
public class SearchController {

    private final SearchByTagsUseCase searchByTagsUseCase;

    public SearchController(SearchByTagsUseCase searchByTagsUseCase) {
        this.searchByTagsUseCase = searchByTagsUseCase;
    }

    @GetMapping("/tags")
    @Operation(summary = "Buscar conteúdo por tags", description = "Busca artigos, tópicos e comentários que contenham todas as tags especificadas")
    public ResponseEntity<TagSearchPageResponse> searchByTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "ALL") SearchContentType type,
            @RequestParam(defaultValue = "RECENT") FeedSortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(searchByTagsUseCase.search(tags, type, sort, page, size));
    }
}
