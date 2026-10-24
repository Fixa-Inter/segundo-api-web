package com.exemplo.website.service;

import com.exemplo.website.dto.turnousuario.TurnoUsuarioResponse;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Turno;
import com.exemplo.website.model.TurnoUsuario;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.TurnoRepository;
import com.exemplo.website.repository.TurnoUsuarioRepository;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.security.AccessPolicyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TurnoUsuarioService {

    private final TurnoUsuarioRepository turnoUsuarioRepository;
    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AccessPolicyService accessPolicyService;

    @Transactional
    public TurnoUsuarioResponse vincular(Long turnoId, Long usuarioId, Long usuarioAutenticadoId) {
        Turno turno = buscarTurno(turnoId);
        Usuario usuario = buscarUsuario(usuarioId);
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, turno.getUnidade().getId());
        validarElegibilidade(turno, usuario);

        TurnoUsuario vinculo = turnoUsuarioRepository
                .findByTurno_IdAndUsuario_Id(turnoId, usuarioId)
                .orElseGet(() -> {
                    TurnoUsuario novo = new TurnoUsuario();
                    novo.setTurno(turno);
                    novo.setUsuario(usuario);
                    novo.setEstaAtivo(true);
                    return novo;
                });

        if (vinculo.getId() != null
                && Boolean.TRUE.equals(vinculo.getEstaAtivo())) {
            throw new IllegalArgumentException(
                    "O técnico já está vinculado a este turno"
            );
        }

        vinculo.setEstaAtivo(true);
        return toResponse(turnoUsuarioRepository.save(vinculo));
    }

    @Transactional(readOnly = true)
    public List<TurnoUsuarioResponse> listarUsuariosDoTurno(Long turnoId, Long usuarioAutenticadoId) {
        Turno turno = buscarTurno(turnoId);
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, turno.getUnidade().getId());
        if (!turnoRepository.existsById(turnoId)) {
            throw new EntityNotFoundException("Turno não encontrado");
        }

        return turnoUsuarioRepository
                .findByTurno_IdAndEstaAtivoTrue(turnoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TurnoUsuarioResponse> listarTurnosDoUsuario(Long usuarioId, Long usuarioAutenticadoId) {
        Usuario usuario = buscarUsuario(usuarioId);
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, usuario.getUnidade().getId());
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        return turnoUsuarioRepository
                .findByUsuario_IdAndEstaAtivoTrue(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TurnoUsuarioResponse ativar(Long id, Long usuarioAutenticadoId) {
        TurnoUsuario vinculo = buscarVinculo(id);
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, vinculo.getTurno().getUnidade().getId());
        validarElegibilidade(vinculo.getTurno(), vinculo.getUsuario());
        vinculo.setEstaAtivo(true);
        return toResponse(turnoUsuarioRepository.save(vinculo));
    }

    @Transactional
    public TurnoUsuarioResponse desativar(Long id, Long usuarioAutenticadoId) {
        TurnoUsuario vinculo = buscarVinculo(id);
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, vinculo.getTurno().getUnidade().getId());
        vinculo.setEstaAtivo(false);
        return toResponse(turnoUsuarioRepository.save(vinculo));
    }

    private Turno buscarTurno(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Turno não encontrado"
                ));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado"
                ));
    }

    private TurnoUsuario buscarVinculo(Long id) {
        return turnoUsuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vínculo entre turno e usuário não encontrado"
                ));
    }

    private void validarElegibilidade(Turno turno, Usuario usuario) {
        if (!Boolean.TRUE.equals(turno.getEstaAtivo())) {
            throw new IllegalArgumentException("O turno está inativo");
        }

        if (!Boolean.TRUE.equals(usuario.getEstaAtivo())) {
            throw new IllegalArgumentException("O usuário está inativo");
        }

        if (usuario.getTipoAcesso() != TipoAcesso.TECNICO) {
            throw new IllegalArgumentException(
                    "Somente usuários do tipo TÉCNICO podem ser vinculados a turnos"
            );
        }

        Long unidadeDoTurno = turno.getUnidade().getId();
        Long unidadeDoUsuario = usuario.getUnidade().getId();
        if (!Objects.equals(unidadeDoTurno, unidadeDoUsuario)) {
            throw new IllegalArgumentException(
                    "O turno e o técnico devem pertencer à mesma unidade"
            );
        }
    }

    private TurnoUsuarioResponse toResponse(TurnoUsuario vinculo) {
        return new TurnoUsuarioResponse(
                vinculo.getId(),
                vinculo.getTurno().getId(),
                vinculo.getTurno().getNome(),
                vinculo.getUsuario().getId(),
                vinculo.getUsuario().getNomeCompleto(),
                vinculo.getTurno().getUnidade().getId(),
                vinculo.getDataCriacao(),
                vinculo.getEstaAtivo()
        );
    }
}
