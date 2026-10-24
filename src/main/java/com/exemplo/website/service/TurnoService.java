package com.exemplo.website.service;

import com.exemplo.website.dto.turno.TurnoRequest;
import com.exemplo.website.dto.turno.TurnoResponse;
import com.exemplo.website.dto.turno.TurnoUpdateRequest;
import com.exemplo.website.model.Unidade;
import com.exemplo.website.model.Turno;
import com.exemplo.website.repository.UnidadeRepository;
import com.exemplo.website.repository.TurnoRepository;
import com.exemplo.website.security.AccessPolicyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TurnoService {

    private final UnidadeRepository unidadeRepository;
    private final TurnoRepository turnoRepository;
    private final AccessPolicyService accessPolicyService;

    public TurnoResponse cadastrar(TurnoRequest request, Long usuarioAutenticadoId) {

        validarHoras(
                request.horaInicio(),
                request.horaFim(),
                request.atravessaMeiaNoite()
        );

        Unidade unidade =
                unidadeRepository
                        .findById(request.unidadeId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Unidade não encontrada"
                                )
                        );

        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, unidade.getId());

        Turno turno = new Turno();

        turno.setUnidade(unidade);
        turno.setNome(request.nome().trim());
        turno.setHoraInicio(request.horaInicio());
        turno.setHoraFim(request.horaFim());
        turno.setAtravessaMeiaNoite(
                request.atravessaMeiaNoite()
        );

        return toResponse(
                turnoRepository.save(turno)
        );
    }

    public List<TurnoResponse> listarTodos(Long usuarioAutenticadoId) {
        List<Turno> turnos = accessPolicyService.ehAdministrador(usuarioAutenticadoId)
                ? turnoRepository.findAll()
                : turnoRepository.findByUnidade_Id(accessPolicyService.unidadeDoUsuarioAutenticado(usuarioAutenticadoId));
        return turnos
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TurnoResponse> listarPorUnidade(
            Long unidadeId,
            Long usuarioAutenticadoId
    ) {

        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, unidadeId);

        return turnoRepository
                .findByUnidade_Id(unidadeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TurnoResponse buscarPorId(Long id, Long usuarioAutenticadoId) {
        Turno turno = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, turno);
        return toResponse(turno);
    }

    public TurnoResponse atualizar(
            Long id,
            TurnoUpdateRequest request,
            Long usuarioAutenticadoId
    ) {

        Turno turno = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, turno);

        if (request.unidadeId() != null) {

            Unidade unidade =
                    unidadeRepository
                            .findById(request.unidadeId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Unidade não encontrada"
                                    )
                            );

            accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, unidade.getId());

            turno.setUnidade(unidade);
        }

        if (request.nome() != null) {
            turno.setNome(request.nome().trim());
        }

        Integer inicio =
                request.horaInicio() != null
                        ? request.horaInicio()
                        : turno.getHoraInicio();

        Integer fim =
                request.horaFim() != null
                        ? request.horaFim()
                        : turno.getHoraFim();

        Boolean atravessaMeiaNoite =
                request.atravessaMeiaNoite() != null
                        ? request.atravessaMeiaNoite()
                        : turno.getAtravessaMeiaNoite();

        validarHoras(inicio, fim, atravessaMeiaNoite);

        turno.setHoraInicio(inicio);
        turno.setHoraFim(fim);

        turno.setAtravessaMeiaNoite(atravessaMeiaNoite);

        return toResponse(
                turnoRepository.save(turno)
        );
    }

    public TurnoResponse ativar(Long id, Long usuarioAutenticadoId) {

        Turno turno = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, turno);

        turno.setEstaAtivo(true);

        return toResponse(
                turnoRepository.save(turno)
        );
    }

    public TurnoResponse desativar(Long id, Long usuarioAutenticadoId) {

        Turno turno = buscarEntidade(id);
        validarAcesso(usuarioAutenticadoId, turno);

        turno.setEstaAtivo(false);

        return toResponse(
                turnoRepository.save(turno)
        );
    }

    private Turno buscarEntidade(Long id) {

        return turnoRepository
                .findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Turno não encontrado"
                        )
                );
    }

    private void validarAcesso(Long usuarioAutenticadoId, Turno turno) {
        accessPolicyService.exigirAcessoUnidade(usuarioAutenticadoId, turno.getUnidade().getId());
    }

    private void validarHoras(
            Integer horaInicio,
            Integer horaFim,
            Boolean atravessaMeiaNoite
    ) {

        if (
                horaInicio == null ||
                        horaFim == null
        ) {
            throw new IllegalArgumentException(
                    "Os horários são obrigatórios"
            );
        }

        if (atravessaMeiaNoite == null) {
            throw new IllegalArgumentException(
                    "Informe se o turno atravessa a meia-noite"
            );
        }

        if (horaInicio < 0 || horaInicio > 86400 || horaFim < 0 || horaFim > 86400) {
            throw new IllegalArgumentException(
                    "Os horários devem ser informados em segundos entre 0 e 86400"
            );
        }

        if (horaInicio.equals(horaFim)) {
            throw new IllegalArgumentException(
                    "Os horários de início e fim devem ser diferentes"
            );
        }

        boolean horariosAtravessamMeiaNoite = horaFim < horaInicio;

        if (horariosAtravessamMeiaNoite != atravessaMeiaNoite) {
            throw new IllegalArgumentException(
                    "Os horários informados não correspondem à indicação de atravessar a meia-noite"
            );
        }
    }

    private TurnoResponse toResponse(Turno turno) {

        return new TurnoResponse(
                turno.getId(),
                turno.getUnidade().getId(),
                turno.getNome(),
                turno.getHoraInicio(),
                turno.getHoraFim(),
                turno.getAtravessaMeiaNoite(),
                turno.getDataCriacao(),
                turno.getEstaAtivo()
        );
    }
}
