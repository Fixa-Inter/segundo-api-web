package com.exemplo.website.service;

import com.exemplo.website.dto.aptidao.AptidaoRequest;
import com.exemplo.website.dto.aptidao.AptidaoResponse;
import com.exemplo.website.dto.aptidao.AptidaoUpdateRequest;
import com.exemplo.website.model.Aptidao;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.AptidaoRepository;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.security.AccessPolicyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AptidaoService {

    private final AptidaoRepository aptidaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AccessPolicyService accessPolicyService;

    public AptidaoResponse cadastrar(AptidaoRequest request, Long usuarioAutenticadoId) {

        validarNota(request.nota());

        Usuario usuario = usuarioRepository
                .findById(request.usuarioId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, usuario.getUnidade().getId());

        if (aptidaoRepository
                .existsByUsuario_IdAndCategoriaProblema(
                        request.usuarioId(),
                        request.categoriaProblema()
                )) {
            throw new IllegalArgumentException(
                    "O usuário já possui aptidão para essa categoria"
            );
        }

        Aptidao aptidao = new Aptidao();

        aptidao.setUsuario(usuario);
        aptidao.setCategoriaProblema(
                request.categoriaProblema()
        );
        aptidao.setNota(request.nota());
        aptidao.setEstaAtivo(true);

        return toResponse(
                aptidaoRepository.save(aptidao)
        );
    }

    public List<AptidaoResponse> listarPorUsuario(Long usuarioId, Long usuarioAutenticadoId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, usuario.getUnidade().getId());

        return aptidaoRepository
                .findByUsuario_Id(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AptidaoResponse buscarPorId(Long id, Long usuarioAutenticadoId) {
        Aptidao aptidao = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, aptidao);
        return toResponse(aptidao);
    }

    public AptidaoResponse atualizar(
            Long id,
            AptidaoUpdateRequest request,
            Long usuarioAutenticadoId
    ) {

        Aptidao aptidao = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, aptidao);

        if (request.nota() != null) {
            validarNota(request.nota());

            aptidao.setNota(request.nota());
        }

        if (request.estaAtivo() != null) {
            aptidao.setEstaAtivo(request.estaAtivo());
        }

        return toResponse(
                aptidaoRepository.save(aptidao)
        );
    }

    public AptidaoResponse ativar(Long id, Long usuarioAutenticadoId) {

        Aptidao aptidao = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, aptidao);

        aptidao.setEstaAtivo(true);

        return toResponse(
                aptidaoRepository.save(aptidao)
        );
    }

    public AptidaoResponse desativar(Long id, Long usuarioAutenticadoId) {

        Aptidao aptidao = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, aptidao);

        aptidao.setEstaAtivo(false);

        return toResponse(
                aptidaoRepository.save(aptidao)
        );
    }

    private Aptidao buscarEntidade(Long id) {

        return aptidaoRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Aptidão não encontrada"
                        )
                );
    }

    private void validarAcesso(Long usuarioAutenticadoId, Aptidao aptidao) {
        accessPolicyService.exigirAcessoUnidade(
                usuarioAutenticadoId,
                aptidao.getUsuario().getUnidade().getId()
        );
    }

    private void validarNota(BigDecimal nota) {

        if (nota == null
                || nota.compareTo(BigDecimal.ZERO) < 0
                || nota.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException(
                    "A nota da aptidão deve estar entre 0 e 10"
            );
        }
    }

    private AptidaoResponse toResponse(
            Aptidao aptidao
    ) {

        return new AptidaoResponse(
                aptidao.getId(),
                aptidao.getUsuario().getId(),
                aptidao.getCategoriaProblema(),
                aptidao.getNota(),
                aptidao.getDataCriacao(),
                aptidao.getEstaAtivo()
        );
    }
}
