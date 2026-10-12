package com.exemplo.website.service;

import com.exemplo.website.dto.turno.TurnoRequest;
import com.exemplo.website.dto.turno.TurnoResponse;
import com.exemplo.website.dto.turno.TurnoUpdateRequest;
import com.exemplo.website.model.Endereco;
import com.exemplo.website.model.Turno;
import com.exemplo.website.repository.EnderecoRepository;
import com.exemplo.website.repository.TurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final EnderecoRepository enderecoRepository;
    private final TurnoRepository turnoRepository;

    public TurnoResponse cadastrar(TurnoRequest request) {

        validarHoras(
                request.horaInicio(),
                request.horaFim(),
                request.atravessaMeiaNoite()
        );

        Endereco endereco =
                enderecoRepository
                        .findById(request.enderecoId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Endereço não encontrado"
                                )
                        );

        Turno turno = new Turno();

        turno.setEndereco(endereco);
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

    public List<TurnoResponse> listarTodos() {

        return turnoRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TurnoResponse> listarPorEndereco(
            Long enderecoId
    ) {

        return turnoRepository
                .findByEndereco_Id(enderecoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TurnoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    public TurnoResponse atualizar(
            Long id,
            TurnoUpdateRequest request
    ) {

        Turno turno = buscarEntidade(id);

        if (request.enderecoId() != null) {

            Endereco endereco =
                    enderecoRepository
                            .findById(request.enderecoId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Endereço não encontrado"
                                    )
                            );

            turno.setEndereco(endereco);
        }

        if (request.nome() != null) {
            turno.setNome(request.nome().trim());
        }

        LocalTime inicio =
                request.horaInicio() != null
                        ? request.horaInicio()
                        : turno.getHoraInicio();

        LocalTime fim =
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

    public TurnoResponse ativar(Long id) {

        Turno turno = buscarEntidade(id);

        turno.setEstaAtivo(true);

        return toResponse(
                turnoRepository.save(turno)
        );
    }

    public TurnoResponse desativar(Long id) {

        Turno turno = buscarEntidade(id);

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

    private void validarHoras(
            LocalTime horaInicio,
            LocalTime horaFim,
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

        if (horaInicio.equals(horaFim)) {
            throw new IllegalArgumentException(
                    "Os horários de início e fim devem ser diferentes"
            );
        }

        boolean horariosAtravessamMeiaNoite = horaFim.isBefore(horaInicio);

        if (horariosAtravessamMeiaNoite != atravessaMeiaNoite) {
            throw new IllegalArgumentException(
                    "Os horários informados não correspondem à indicação de atravessar a meia-noite"
            );
        }
    }

    private TurnoResponse toResponse(Turno turno) {

        return new TurnoResponse(
                turno.getId(),
                turno.getEndereco().getId(),
                turno.getNome(),
                turno.getHoraInicio(),
                turno.getHoraFim(),
                turno.getAtravessaMeiaNoite(),
                turno.getDataCriacao(),
                turno.getEstaAtivo()
        );
    }
}
