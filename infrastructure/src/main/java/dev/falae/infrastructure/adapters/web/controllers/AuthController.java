package dev.falae.infrastructure.adapters.web.controllers;

import dev.falae.application.ports.dto.GoogleAuthRequest;
import dev.falae.application.ports.dto.LoginRequest;
import dev.falae.application.ports.dto.LoginResponse;
import dev.falae.application.ports.dto.VerifyEmailResponse;
import dev.falae.application.ports.services.TokenService;
import dev.falae.application.usecases.AuthenticateAdminUseCase;
import dev.falae.application.usecases.AuthenticateAuthorUseCase;
import dev.falae.application.usecases.GoogleAuthUseCase;
import dev.falae.application.usecases.ResendVerificationEmailUseCase;
import dev.falae.application.usecases.VerifyEmailUseCase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${cookie.setSecure}")
    private boolean setSecure;

    private final AuthenticateAuthorUseCase authenticateAuthorUseCase;
    private final AuthenticateAdminUseCase authenticateAdminUseCase;
    private final GoogleAuthUseCase googleAuthUseCase;
    private final TokenService tokenService;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final ResendVerificationEmailUseCase resendVerificationEmailUseCase;

    public AuthController(AuthenticateAuthorUseCase authenticateAuthorUseCase,
                          AuthenticateAdminUseCase authenticateAdminUseCase,
                          GoogleAuthUseCase googleAuthUseCase,
                          TokenService tokenService,
                          VerifyEmailUseCase verifyEmailUseCase,
                          ResendVerificationEmailUseCase resendVerificationEmailUseCase) {
        this.authenticateAuthorUseCase = authenticateAuthorUseCase;
        this.authenticateAdminUseCase = authenticateAdminUseCase;
        this.googleAuthUseCase = googleAuthUseCase;
        this.tokenService = tokenService;
        this.verifyEmailUseCase = verifyEmailUseCase;
        this.resendVerificationEmailUseCase = resendVerificationEmailUseCase;
    }

    @PostMapping("/author/login")
    public ResponseEntity<LoginResponse> loginAuthor(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authenticateAuthorUseCase.login(request);
        setTokenCookie(response, loginResponse.token());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/admin/login")
    @Operation(summary = "Autentica um administrador")
    public ResponseEntity<LoginResponse> loginAdmin(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authenticateAdminUseCase.login(request);
        setTokenCookie(response, loginResponse.token());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/google")
    @Operation(summary = "Autentica ou cadastra um autor via Google")
    public ResponseEntity<LoginResponse> googleAuth(@Valid @RequestBody GoogleAuthRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = googleAuthUseCase.authenticate(request);
        setTokenCookie(response, loginResponse.token());
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(HttpServletRequest request) {
        String token = extractTokenFromCookies(request);
        if (token != null && tokenService.validateToken(token)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(setSecure)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verifica o email do autor e retorna token de login")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestParam String token, HttpServletResponse response) {
        VerifyEmailResponse verifyResponse = verifyEmailUseCase.verify(token);
        setTokenCookie(response, verifyResponse.token());
        return ResponseEntity.ok(verifyResponse);
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Reenvia o email de verificacao")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam String email) {
        resendVerificationEmailUseCase.resend(email);
        return ResponseEntity.ok("Verification email sent successfully");
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(setSecure)
                .path("/")
                .maxAge(24 * 60 * 60) //24 hours
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
