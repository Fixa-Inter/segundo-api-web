package com.exemplo.website.infrastructure.notification;

import com.exemplo.website.domain.model.Notificacao;
import com.exemplo.website.domain.repository.TokenDispositivoRepository;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/** Envia push aos dispositivos vinculados ao destinatário da notificação. */
@Component
public class FirebasePushNotificationSender {
    private static final Logger log = LoggerFactory.getLogger(FirebasePushNotificationSender.class);
    private static final int MAX_TOKENS_PER_BATCH = 500;

    private final FirebaseConfiguration firebaseConfiguration;
    private final TokenDispositivoRepository tokenRepository;

    public FirebasePushNotificationSender(
            FirebaseConfiguration firebaseConfiguration,
            TokenDispositivoRepository tokenRepository
    ) {
        this.firebaseConfiguration = firebaseConfiguration;
        this.tokenRepository = tokenRepository;
    }

    public void enviar(Notificacao notificacao) {
        try {
            List<String> tokens = tokenRepository.listarPorUsuario(notificacao.usuarioId());
            if (tokens.isEmpty()) return;

            for (int inicio = 0; inicio < tokens.size(); inicio += MAX_TOKENS_PER_BATCH) {
                List<String> lote = tokens.subList(inicio, Math.min(inicio + MAX_TOKENS_PER_BATCH, tokens.size()));
                MulticastMessage mensagem = MulticastMessage.builder()
                        .addAllTokens(lote)
                        .setNotification(Notification.builder().setTitle("FIXA").setBody(notificacao.mensagem()).build())
                        .putAllData(Map.of(
                                "notificacaoId", notificacao.id(),
                                "tipoNotificacao", Integer.toString(notificacao.tipoNotificacao()),
                                "referenciaId", notificacao.referenciaId()
                        ))
                        .build();
                BatchResponse resultado = FirebaseMessaging.getInstance(firebaseConfiguration.getApp())
                        .sendEachForMulticast(mensagem);
                log.info("Push de evento {} enviado ao usuário {}: {} sucesso(s), {} falha(s).",
                        notificacao.referenciaId(), notificacao.usuarioId(),
                        resultado.getSuccessCount(), resultado.getFailureCount());
            }
        } catch (Exception exception) {
            log.warn("Não foi possível enviar push de evento {} ao usuário {}.",
                    notificacao.referenciaId(), notificacao.usuarioId(), exception);
        }
    }
}
