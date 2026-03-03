package dev.falae.application.ports.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAuthorProfileRequest(
        @Email(message = "Email deve ser válido")
        String email,

        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password,

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name,

        @Size(min = 3, max = 30, message = "O userName deve ter entre 3 e 30 caracteres")
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "O userName deve conter apenas letras minúsculas, números e hífens"
        )
        String userName,

        @Pattern(
                regexp = "^(https://github\\.com/[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?)?$",
                message = "O gitHub deve ser uma URL válida de perfil do GitHub (ex: https://github.com/usuario)"
        )
        String gitHub,

        String profileImageUrl,

        @Size(max = 500, message = "A bio deve ter no máximo 500 caracteres")
        String bio
) {}
