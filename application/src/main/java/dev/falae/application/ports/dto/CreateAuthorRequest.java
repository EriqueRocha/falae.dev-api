package dev.falae.application.ports.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAuthorRequest(
        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "O email deve ser válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String password,

        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @NotBlank(message = "O userName é obrigatório.")
        @Size(min = 3, max = 30, message = "O userName deve ter entre 3 e 30 caracteres.")
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "O userName deve conter apenas letras minúsculas, números e hífens."
        )
        String userName
) {}
