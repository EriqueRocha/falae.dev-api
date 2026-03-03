package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.AuthorContentSortType;
import dev.falae.application.ports.dto.CreateTopicRequest;
import dev.falae.application.ports.dto.CreateTopicResponse;
import dev.falae.application.ports.dto.EditTopicRequest;
import dev.falae.application.ports.dto.FeedSortType;
import dev.falae.application.ports.dto.InteractionResponse;
import dev.falae.application.ports.dto.TopicPageResponse;
import dev.falae.application.ports.dto.TopicResponse;
import dev.falae.application.usecases.CreateTopicUseCase;
import dev.falae.application.usecases.DeleteTopicUseCase;
import dev.falae.application.usecases.EditTopicUseCase;
import dev.falae.application.usecases.FindTopicUseCase;
import dev.falae.application.usecases.FindTopicsUseCase;
import dev.falae.application.usecases.TopicInteractionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/topic")
public class TopicController {

    private final CreateTopicUseCase createTopicUseCase;
    private final EditTopicUseCase editTopicUseCase;
    private final DeleteTopicUseCase deleteTopicUseCase;
    private final FindTopicUseCase findTopicUseCase;
    private final FindTopicsUseCase findTopicsUseCase;
    private final TopicInteractionUseCase topicInteractionUseCase;

    public TopicController(CreateTopicUseCase createTopicUseCase, EditTopicUseCase editTopicUseCase, DeleteTopicUseCase deleteTopicUseCase, FindTopicUseCase findTopicUseCase, FindTopicsUseCase findTopicsUseCase, TopicInteractionUseCase topicInteractionUseCase){
        this.createTopicUseCase = createTopicUseCase;
        this.editTopicUseCase = editTopicUseCase;
        this.deleteTopicUseCase = deleteTopicUseCase;
        this.findTopicUseCase = findTopicUseCase;
        this.findTopicsUseCase = findTopicsUseCase;
        this.topicInteractionUseCase = topicInteractionUseCase;
    }

    @PostMapping(value = "/saveNew")
    @Operation(summary = "Cadastrar um tópico (author)")
    public ResponseEntity<CreateTopicResponse> saveNew(@Valid @RequestBody CreateTopicRequest createTopicRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(createTopicUseCase.create(createTopicRequest));
    }

    @PutMapping(value = "/edit")
    @Operation(summary = "Editar um tópico (author)")
    public ResponseEntity<Void> edit(@Valid @RequestBody EditTopicRequest editTopicRequest) {
        editTopicUseCase.edit(editTopicRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Listar tópicos paginados")
    public ResponseEntity<TopicPageResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RECENT") FeedSortType sort) {
        return ResponseEntity.ok(findTopicsUseCase.findAll(page, size, sort));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar tópicos por título")
    public ResponseEntity<TopicPageResponse> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "RECENT") AuthorContentSortType sort) {
        return ResponseEntity.ok(findTopicsUseCase.searchByTitle(title, page, size, sort));
    }

    @GetMapping(value = "/{topicId}")
    @Operation(summary = "Buscar um tópico por ID")
    public ResponseEntity<TopicResponse> findById(@PathVariable UUID topicId) {
        return ResponseEntity.ok(findTopicUseCase.findById(topicId));
    }

    @GetMapping(value = "/{userName}/{slug}")
    @Operation(summary = "Buscar um tópico por userName e slug")
    public ResponseEntity<TopicResponse> findByUserNameAndSlug(
            @PathVariable String userName,
            @PathVariable String slug) {
        return ResponseEntity.ok(findTopicUseCase.findByAuthorUserNameAndSlug(userName, slug));
    }

    @DeleteMapping(value = "/delete/{topicId}")
    @Operation(summary = "Deleta um tópico (author)")
    public ResponseEntity<Void> delete(@PathVariable UUID topicId){
        deleteTopicUseCase.delete(topicId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{topicId}/like")
    @Operation(summary = "Curtir/descurtir um tópico (author)")
    public ResponseEntity<InteractionResponse> toggleLike(@PathVariable UUID topicId) {
        return ResponseEntity.ok(topicInteractionUseCase.toggleLike(topicId));
    }

    @PostMapping(value = "/{topicId}/dislike")
    @Operation(summary = "Dar/remover dislike em um tópico (author)")
    public ResponseEntity<InteractionResponse> toggleDislike(@PathVariable UUID topicId) {
        return ResponseEntity.ok(topicInteractionUseCase.toggleDislike(topicId));
    }
}
