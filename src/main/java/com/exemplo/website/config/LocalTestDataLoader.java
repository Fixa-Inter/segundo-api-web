package com.exemplo.website.config;

import com.exemplo.website.model.Endereco;
import com.exemplo.website.model.Instituicao;
import com.exemplo.website.model.LocalEndereco;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Enum.TipoInstituicao;
import com.exemplo.website.model.Enum.TipoLocalEndereco;
import com.exemplo.website.repository.LocalEnderecoRepository;
import com.exemplo.website.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("localtest")
public class LocalTestDataLoader implements CommandLineRunner {

    private final EntityManager entityManager;
    private final UsuarioRepository usuarioRepository;
    private final LocalEnderecoRepository localEnderecoRepository;
    private final PasswordEncoder passwordEncoder;

    public LocalTestDataLoader(
            EntityManager entityManager,
            UsuarioRepository usuarioRepository,
            LocalEnderecoRepository localEnderecoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.entityManager = entityManager;
        this.usuarioRepository = usuarioRepository;
        this.localEnderecoRepository = localEnderecoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (usuarioRepository.count() > 0) {
            return;
        }

        Instituicao instituicao = new Instituicao();
        instituicao.setNome("Instituição Teste");
        instituicao.setDominioEmail("teste.com");
        instituicao.setTipoInstituicao(TipoInstituicao.ESCOLA);
        instituicao.setEstaAtivo(true);

        entityManager.persist(instituicao);

        Endereco endereco = new Endereco();
        endereco.setInstituicao(instituicao);
        endereco.setLogradouro("Rua Teste");
        endereco.setNumero("100");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");
        endereco.setCep("01001000");
        endereco.setCnpj("12345678000199");
        endereco.setEstaAtivo(true);

        entityManager.persist(endereco);

        Usuario usuario = new Usuario();
        usuario.setEndereco(endereco);
        usuario.setNomeCompleto("Gestor Teste");
        usuario.setEmail("gestor@teste.com");
        usuario.setTipoAcesso(TipoAcesso.GESTOR);
        usuario.setCargo("Gestor");
        usuario.setSenhaHash(passwordEncoder.encode("Senha123"));
        usuario.setDataNascimento(LocalDate.of(2000, 1, 1));
        usuario.setEstaAtivo(true);
        usuario.setPrimeiroAcesso(false);

        usuarioRepository.save(usuario);

        LocalEndereco localEndereco = new LocalEndereco();
        localEndereco.setEndereco(endereco);
        localEndereco.setNome("Sala 101");
        localEndereco.setTipoLocalEndereco(TipoLocalEndereco.SALA_AULA);
        localEndereco.setDescricao("Sala de teste");
        localEndereco.setEstaAtivo(true);

        localEnderecoRepository.save(localEndereco);
    }
}
