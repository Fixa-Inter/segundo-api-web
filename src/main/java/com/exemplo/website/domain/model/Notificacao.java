package com.exemplo.website.domain.model;

import java.time.LocalDateTime;

/** Modelo interno da notificação persistida no keyspace compartilhado do FIXA. */
public record Notificacao(
        String id,
        Long usuarioId,
        int tipoNotificacao,
        String referenciaId,
        String mensagem,
        LocalDateTime dataCriacao,
        boolean visualizada
) { }
