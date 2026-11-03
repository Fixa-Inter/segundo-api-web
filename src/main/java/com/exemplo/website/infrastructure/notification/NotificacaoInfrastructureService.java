package com.exemplo.website.infrastructure.notification;

import com.exemplo.website.domain.service.NotificacaoSender;
import com.exemplo.website.domain.model.Notificacao;
import com.exemplo.website.domain.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.UUID;

/** Persiste a notificação após o commit e delega o push sem bloquear a transação de negócio. */
@Component
@RequiredArgsConstructor
public class NotificacaoInfrastructureService implements NotificacaoSender {
    private static final Logger log = LoggerFactory.getLogger(NotificacaoInfrastructureService.class);
    private final NotificacaoRepository repository;
    private final NotificacaoPushAsyncDispatcher dispatcher;

    @Override
    public void notificarAposPersistencia(Long usuarioId, int tipo, Long referenciaId, String mensagem) {
        if (usuarioId == null || mensagem == null || mensagem.isBlank()) return;
        Runnable enviar = () -> persistirEEnviar(usuarioId, tipo, referenciaId, mensagem);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { enviar.run(); }
            });
        } else {
            enviar.run();
        }
    }

    private void persistirEEnviar(Long usuarioId, int tipo, Long referenciaId, String mensagem) {
        Notificacao notificacao = new Notificacao(UUID.randomUUID().toString(), usuarioId, tipo,
                String.valueOf(referenciaId), mensagem, LocalDateTime.now(), false);
        try {
            repository.salvar(notificacao);
        } catch (Exception exception) {
            log.warn("Não foi possível salvar notificação de evento {} no Redis para usuário {}.",
                    referenciaId, usuarioId, exception);
        }
        dispatcher.enviar(notificacao);
    }
}
