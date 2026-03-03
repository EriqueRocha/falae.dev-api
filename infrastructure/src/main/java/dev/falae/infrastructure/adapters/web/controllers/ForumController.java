package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.ForumStatsResponse;
import dev.falae.application.usecases.GetForumStatsUseCase;
import dev.falae.infrastructure.config.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    private final GetForumStatsUseCase getForumStatsUseCase;

    public ForumController(GetForumStatsUseCase getForumStatsUseCase) {
        this.getForumStatsUseCase = getForumStatsUseCase;
    }

    @GetMapping("/stats")
    @Cacheable(CacheConfig.FORUM_STATS_CACHE)
    public ResponseEntity<ForumStatsResponse> getForumStats() {
        return ResponseEntity.ok(getForumStatsUseCase.getStats());
    }
}
