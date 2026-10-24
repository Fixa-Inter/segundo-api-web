package com.exemplo.website.infrastructure.swagger;

public record SwaggerEndpoint(
        String method,
        String path,
        String tag,
        String summary,
        String description
) {
}
