package com.exemplo.website.dto.dashboard;

public record DashboardEmbedResponse(
        String instanceUrl,
        String workspaceId,
        String dashboardId,
        String token,
        Long expiresIn
) {
}
