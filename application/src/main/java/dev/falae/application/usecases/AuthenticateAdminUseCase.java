package dev.falae.application.usecases;

import dev.falae.application.exceptions.AuthenticationException;
import dev.falae.application.ports.dto.LoginRequest;
import dev.falae.application.ports.dto.LoginResponse;
import dev.falae.application.ports.repositories.AdminRepository;
import dev.falae.application.ports.services.PasswordService;
import dev.falae.application.ports.services.TokenService;
import dev.falae.core.domain.entities.Admin;

public class AuthenticateAdminUseCase {

    private final AdminRepository adminRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;

    public AuthenticateAdminUseCase(AdminRepository adminRepository,
                                     PasswordService passwordService,
                                     TokenService tokenService) {
        this.adminRepository = adminRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.email());

        if (!passwordService.matches(request.password(), admin.getPassword())) {
            throw new AuthenticationException();
        }

        String token = tokenService.generateToken(admin.getId(), admin.getEmail(), admin.getRole());

        return new LoginResponse(
                "Login successful",
                admin.getRole(),
                admin.getEmail(),
                null,
                null,
                admin.getName(),
                token,
                true // Admin accounts are always verified
        );
    }
}
