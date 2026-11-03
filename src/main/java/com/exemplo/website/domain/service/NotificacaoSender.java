package com.exemplo.website.domain.service;

/** Contrato para registrar e enviar notificação após uma operação persistida. */
public interface NotificacaoSender {
    void notificarAposPersistencia(Long usuarioId, int tipo, Long referenciaId, String mensagem);
}
