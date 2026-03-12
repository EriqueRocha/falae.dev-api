package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.AuthorInteractionPageResponse;
import dev.falae.application.ports.dto.UnreadCountResponse;
import dev.falae.application.usecases.GetAuthorInteractionsUseCase;
import dev.falae.application.usecases.GetUnreadCountUseCase;
import dev.falae.application.usecases.MarkAllInteractionsReadUseCase;
import dev.falae.application.usecases.MarkInteractionReadUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/authors/me/interactions")
@Tag(name = "Interactions", description = "Author interactions/notifications endpoints")
public class InteractionController {

    private final GetAuthorInteractionsUseCase getAuthorInteractionsUseCase;
    private final GetUnreadCountUseCase getUnreadCountUseCase;
    private final MarkInteractionReadUseCase markInteractionReadUseCase;
    private final MarkAllInteractionsReadUseCase markAllInteractionsReadUseCase;

    public InteractionController(GetAuthorInteractionsUseCase getAuthorInteractionsUseCase,
                                  GetUnreadCountUseCase getUnreadCountUseCase,
                                  MarkInteractionReadUseCase markInteractionReadUseCase,
                                  MarkAllInteractionsReadUseCase markAllInteractionsReadUseCase) {
        this.getAuthorInteractionsUseCase = getAuthorInteractionsUseCase;
        this.getUnreadCountUseCase = getUnreadCountUseCase;
        this.markInteractionReadUseCase = markInteractionReadUseCase;
        this.markAllInteractionsReadUseCase = markAllInteractionsReadUseCase;
    }

    @GetMapping
    @Operation(summary = "Get paginated list of interactions for the current author")
    public ResponseEntity<AuthorInteractionPageResponse> getInteractions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(getAuthorInteractionsUseCase.execute(page, size));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get count of unread interactions")
    public ResponseEntity<UnreadCountResponse> getUnreadCount() {
        return ResponseEntity.ok(getUnreadCountUseCase.execute());
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a specific interaction as read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        markInteractionReadUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all interactions as read")
    public ResponseEntity<Void> markAllAsRead() {
        markAllInteractionsReadUseCase.execute();
        return ResponseEntity.noContent().build();
    }
}
