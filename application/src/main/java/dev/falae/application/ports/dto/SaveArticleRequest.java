package dev.falae.application.ports.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SaveArticleRequest(
    @NotBlank(message = "O título é obrigatório.")
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
    String originalPost
) {
}
