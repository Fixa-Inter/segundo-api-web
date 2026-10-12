package com.exemplo.website.repository;

import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Usuario> findByEmailAndTipoAcesso(
            String email,
            TipoAcesso tipoAcesso
    );

    Optional<Usuario> findByEndereco_CnpjAndTipoAcesso(
            String cnpj,
            TipoAcesso tipoAcesso
    );

    List<Usuario> findByEndereco_Id(Long enderecoId);

    List<Usuario> findByEndereco_IdAndTipoAcesso(
            Long enderecoId,
            TipoAcesso tipoAcesso
    );

}