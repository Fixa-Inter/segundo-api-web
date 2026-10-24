package com.exemplo.website.infrastructure.swagger;

import java.util.List;
import java.util.stream.Stream;

final class WebsiteSwaggerCatalog {
    private WebsiteSwaggerCatalog() {
    }

    static List<SwaggerEndpoint> endpoints() {
        return Stream.of(
                AuthSwagger.endpoints(),
                RecuperacaoSenhaSwagger.endpoints(),
                AptidaoSwagger.endpoints(),
                EventoSwagger.endpoints(),
                InstituicaoSwagger.endpoints(),
                FotoSwagger.endpoints(),
                LocalEnderecoSwagger.endpoints(),
                PerfilSwagger.endpoints(),
                TurnoSwagger.endpoints(),
                TurnoUsuarioSwagger.endpoints(),
                UnidadeSwagger.endpoints(),
                UsuarioSwagger.endpoints()
        ).flatMap(List::stream).toList();
    }
}