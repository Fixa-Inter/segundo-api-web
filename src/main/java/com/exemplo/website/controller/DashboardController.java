package com.exemplo.website.controller;

import com.exemplo.website.dto.dashboard.DashboardEmbedResponse;
import com.exemplo.website.service.DashboardService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('GESTOR', 'ADMINISTRADOR')")
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/embed")
    public ResponseEntity<DashboardEmbedResponse> embed(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(dashboardService.gerarEmbed(extrairUsuarioId(jwt)));
    }

    private Long extrairUsuarioId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getClaimAsString("userId"));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Token não possui um identificador de usuário válido"
            );
        }
    }
}
