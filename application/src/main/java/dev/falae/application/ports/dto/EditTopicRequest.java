package dev.falae.application.ports.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record EditTopicRequest(
    @NotNull(message = "O ID do tópico é obrigatório.")
    UUID topicId,

    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    String title,

    @Size(max = 10000, message = "O conteúdo do tópico deve ter no máximo 10000 caracteres.")
    String topicContent,

    @Size(max = 10, message = "O tópico pode ter no máximo 10 tags.")
    List<@Size(max = 50, message = "Cada tag deve ter no máximo 50 caracteres.")
         @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "Cada tag deve conter apenas letras e números, sem espaços ou caracteres especiais."
    ) String> tags
) {
}
