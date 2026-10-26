package com.exemplo.website.controller;

import com.exemplo.website.dto.auth.LoginRequest;
import com.exemplo.website.dto.auth.LoginResponse;
import com.exemplo.website.dto.auth.RefreshTokenRequest;
import com.exemplo.website.dto.auth.AlterarSenhaRequest;
import com.exemplo.website.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {

        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException exception) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciais inválidas");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            return ResponseEntity.ok(authService.renovar(request.refreshToken()));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token inválido ou expirado");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<Map<String, String>> alterarSenha(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AlterarSenhaRequest request
    ) {
        try {
            long usuarioId = Long.parseLong(jwt.getClaimAsString("userId"));
            authService.alterarSenha(usuarioId, request);
            return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso"));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Credenciais inválidas"));
        } catch (NumberFormatException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Token sem identificador de usuário válido"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(
            Authentication authentication
    ) {

        List<String> roles = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(
                new MeResponse(
                        authentication.getName(),
                        roles
                )
        );
    }

    public record MeResponse(
            String identificador,
            List<String> roles
    ) {
    }
}
