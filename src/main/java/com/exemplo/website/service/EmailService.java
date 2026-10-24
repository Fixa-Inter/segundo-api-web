package com.exemplo.website.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from:noreply@localhost}")
    private String remetente;

    public void enviarCodigoRecuperacao(String email, String nome, int codigo) {
        Locale locale = LocaleContextHolder.getLocale();
        Context contexto = new Context(locale);
        contexto.setVariable("nome", nome);
        contexto.setVariable("codigo", codigo);
        contexto.setVariable("validade", "3 minutos");

        String html = templateEngine.process("email/recuperar-senha", contexto);
        String texto = "Olá, " + nome + "! Seu código para redefinir a senha é "
                + codigo + ". Ele expira em 3 minutos.";

        MimeMessage mensagem = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    mensagem, true, StandardCharsets.UTF_8.name()
            );
            helper.setFrom(remetente);
            helper.setTo(email);
            helper.setSubject("Código para redefinição de senha");
            helper.setText(texto, html);
            mailSender.send(mensagem);
        } catch (MessagingException exception) {
            throw new IllegalStateException("Não foi possível montar o e-mail de recuperação", exception);
        }
    }
}
