package com.exemplo.website.service;

import com.exemplo.website.model.Instituicao;
import com.exemplo.website.model.Unidade;

import java.util.Locale;

/** Regra compartilhada para emails vinculados ao domínio da instituição da unidade. */
public final class EmailInstitucionalValidator {

    private EmailInstitucionalValidator() {
    }

    public static String normalizar(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public static void validar(String email, Unidade unidade) {
        String emailNormalizado = normalizar(email);
        Instituicao instituicao = unidade == null ? null : unidade.getInstituicao();
        String dominioInstituicao = instituicao == null
                ? null
                : normalizar(instituicao.getDominioEmail());
        if (dominioInstituicao != null && dominioInstituicao.startsWith("@")) {
            dominioInstituicao = dominioInstituicao.substring(1);
        }

        int separador = emailNormalizado == null ? -1 : emailNormalizado.lastIndexOf('@');
        String dominioEmail = separador > 0 && separador < emailNormalizado.length() - 1
                ? emailNormalizado.substring(separador + 1)
                : null;
        if (dominioInstituicao == null || !dominioInstituicao.equals(dominioEmail)) {
            throw new IllegalArgumentException(
                    "O email deve usar o domínio da instituição vinculada à unidade"
            );
        }
    }
}
