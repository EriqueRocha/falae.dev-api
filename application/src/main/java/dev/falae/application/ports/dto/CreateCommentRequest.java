package dev.falae.application.ports.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateCommentRequest(
        @NotBlank(message = "O conteúdo do comentário é obrigatório.")
        @Size(max = 3600, message = "O comentário deve ter no máximo 3600 caracteres.")
        String message,

        UUID articleId,
        UUID topicId,
        UUID parentId,

        @Size(max = 10, message = "O comentário pode ter no máximo 10 tags.")
        List<@Size(max = 50, message = "Cada tag deve ter no máximo 50 caracteres.")
             @Pattern(
                regexp = "^[a-zA-Z0-9]+$",
                message = "Cada tag deve conter apenas letras e números, sem espaços ou caracteres especiais."
        ) String> tags
) {}
