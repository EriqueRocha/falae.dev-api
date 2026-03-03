package dev.falae.application.ports.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAuthorRequest(
        String name,

        @Pattern(
                regexp = "^(https://github\\.com/[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?)?$",
                message = "O gitHub deve ser uma URL válida de perfil do GitHub (ex: https://github.com/usuario)."
        )
        String gitHub,

        @Size(max = 500, message = "A bio deve ter no máximo 500 caracteres.")
        String bio
) {}
