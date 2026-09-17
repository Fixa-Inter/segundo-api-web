package com.exemplo.website.repository;


import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailAndTipoAcesso(
            String email,
            TipoAcesso tipoAcesso
    );

    Optional<Usuario> findByEndereco_CnpjAndTipoAcesso(
            String cnpj,
            TipoAcesso tipoAcesso
    );
}