import com.exemplo.website.model.Enum.TipoAcesso;

import java.time.LocalDate;

public record UsuarioUpdateRequest(
        Long enderecoId,
        String nomeCompleto,
        String email,
        TipoAcesso tipoAcesso,
        String cargo,
        LocalDate dataNascimento
) {
}