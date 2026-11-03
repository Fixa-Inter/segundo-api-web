package com.exemplo.website.infrastructure.notification;

import com.exemplo.website.domain.model.Notificacao;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacaoPushAsyncDispatcher {
    private final FirebasePushNotificationSender sender;

    @Async
    public void enviar(Notificacao notificacao) {
        sender.enviar(notificacao);
    }
}
