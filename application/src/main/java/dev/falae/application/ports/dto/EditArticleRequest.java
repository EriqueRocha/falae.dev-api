package dev.falae.application.ports.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record EditArticleRequest(
    @NotNull(message = "O ID do artigo é obrigatório.")
    UUID articleId,

    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    String title,

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    String description,

    @Size(max = 10, message = "O artigo pode ter no máximo 10 tags.")
    List<@Size(max = 50, message = "Cada tag deve ter no máximo 50 caracteres.")
         @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "Cada tag deve conter apenas letras e números, sem espaços ou caracteres especiais."
    ) String> tags,

    @Pattern(
            regexp = "^(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)?$",
            message = "O originalPost deve ser uma URL válida."
    )
    String originalPost,

    List<String> deletedImagePaths
) {
}
