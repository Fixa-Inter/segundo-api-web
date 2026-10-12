package com.exemplo.website.controller;

import com.exemplo.website.dto.auth.LoginRequest;
import com.exemplo.website.dto.auth.LoginResponse;
import com.exemplo.website.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
