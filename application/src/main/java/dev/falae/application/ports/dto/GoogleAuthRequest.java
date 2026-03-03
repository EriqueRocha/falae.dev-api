package dev.falae.application.ports.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(
    @NotBlank(message = "O token do Google e obrigatorio.")
    String credential
) {
}
