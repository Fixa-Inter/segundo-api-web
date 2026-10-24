package com.exemplo.website.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import com.exemplo.website.repository.RefreshTokenRepository;
import com.exemplo.website.model.Enum.TipoAcesso;
import com.exemplo.website.model.Usuario;
import com.exemplo.website.repository.UnidadeRepository;
import com.exemplo.website.repository.UsuarioRepository;
import com.exemplo.website.model.Unidade;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.UUID;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Optional;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthIntegrationTest.TestBeans.class)
@Transactional
@EnabledIfSystemProperty(named = "aiven.integration", matches = "true")
class AuthIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UnidadeRepository unidadeRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final String marker = "codex-it-" + UUID.randomUUID().toString().replace("-", "");
    private String gestorEmail;
    private String dominioEmail;
    private String cnpj;
    private Long unidadeId;

    @BeforeEach
    void criarGestorTemporarioNoAiven() {
        Unidade unidade = unidadeRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getEstaAtivo()))
                .filter(u -> u.getInstituicao() != null
                        && Boolean.TRUE.equals(u.getInstituicao().getEstaAtivo()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "O Aiven precisa ter ao menos uma unidade e instituição ativas para os testes"));

        unidadeId = unidade.getId();
        cnpj = unidade.getCnpj();
        dominioEmail = unidade.getInstituicao().getDominioEmail().replaceFirst("^@", "");
        gestorEmail = marker + "@" + dominioEmail;

        Usuario gestor = new Usuario();
        gestor.setUnidade(unidade);
        gestor.setNomeCompleto("Gestor " + marker);
        gestor.setEmail(gestorEmail);
        gestor.setTipoAcesso(TipoAcesso.GESTOR);
        gestor.setCargo("Gestor de teste automatizado");
        gestor.setSenhaHash(passwordEncoder.encode("Senha123"));
        gestor.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
        gestor.setEstaAtivo(true);
        gestor.setPrimeiroAcesso(false);
        usuarioRepository.saveAndFlush(gestor);
    }

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry registry) {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        String secret = Base64.getEncoder().encodeToString(key);
        registry.add("security.jwt.secret", () -> secret);
    }

    private MockHttpServletResponse login(String email, String password, String cnpj) throws Exception {
        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"senha\":\"" + password
                        + "\",\"cnpj\":\"" + cnpj + "\"}"))
                .andReturn().getResponse();
    }

    private MockHttpServletResponse postJson(String path, String body) throws Exception {
        return mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse();
    }

    private MockHttpServletResponse me(String token) throws Exception {
        var request = org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/auth/me");
        if (token != null) request.header("Authorization", "Bearer " + token);
        return mockMvc.perform(request).andReturn().getResponse();
    }

    private MockHttpServletResponse get(String path, String token) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(path)
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse();
    }

    private void assertLoginAndRole(String email, String role) throws Exception {
        var response = login(email, "Senha123", cnpj);
        assertEquals(200, response.getStatus());
        var matcher = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(response.getContentAsString());
        assertTrue(matcher.find());
        var profile = me(matcher.group(1));
        assertEquals(200, profile.getStatus());
        assertTrue(profile.getContentAsString().contains("\"" + role + "\""));
    }

    private String cnpjTemporario() {
        long numero = Math.floorMod(System.nanoTime(), 10_000_000_000_000L);
        return "9" + String.format("%013d", numero);
    }

    @Test
    void gestorUsesEmail() throws Exception {
        assertLoginAndRole(gestorEmail, "ROLE_GESTOR");
    }

    @Test
    void rejectsUnregisteredUserAndWrongPassword() throws Exception {
        assertEquals(401, login(gestorEmail, "errada", cnpj).getStatus());
        assertEquals(401, login("nao-existe-" + marker + "@" + dominioEmail, "Senha123", cnpj).getStatus());
    }

    @Test
    void refreshRotatesTokenAndRejectsReplay() throws Exception {
        var login = login(gestorEmail, "Senha123", cnpj);
        assertEquals(200, login.getStatus());
        String refreshToken = jsonString(login.getContentAsString(), "refreshToken");

        var refresh = postJson("/auth/refresh", "{\"refreshToken\":\"" + refreshToken + "\"}");
        assertEquals(200, refresh.getStatus());
        String newRefreshToken = jsonString(refresh.getContentAsString(), "refreshToken");
        assertNotEquals(refreshToken, newRefreshToken);

        var replay = postJson("/auth/refresh", "{\"refreshToken\":\"" + refreshToken + "\"}");
        assertEquals(401, replay.getStatus());

        var logout = postJson("/auth/logout", "{\"refreshToken\":\"" + newRefreshToken + "\"}");
        assertEquals(204, logout.getStatus());
        var afterLogout = postJson("/auth/refresh", "{\"refreshToken\":\"" + newRefreshToken + "\"}");
        assertEquals(401, afterLogout.getStatus());
    }

    @Test
    void missingAndInvalidTokensAreRejected() throws Exception {
        assertEquals(401, me(null).getStatus());
        assertEquals(401, me("invalid.token.value").getStatus());
    }

    @Test
    void gestorCannotManageInstitutions() throws Exception {
        var login = login(gestorEmail, "Senha123", cnpj);
        String accessToken = jsonString(login.getContentAsString(), "token");
        assertEquals(403, get("/api/v1/instituicoes", accessToken).getStatus());
    }

    @Test
    void openApiDocumentsEndpointsAndJwtSecurityScheme() throws Exception {
        var response = mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v3/api-docs"))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("API Web"));
        assertTrue(response.getContentAsString().contains("/auth/login"));
        assertTrue(response.getContentAsString().contains("bearerAuth"));
    }

    @Test
    void gestorCrudHttpEndpointsWorkAgainstPostgreSql() throws Exception {
        var loginResponse = login(gestorEmail, "Senha123", cnpj);
        assertEquals(200, loginResponse.getStatus());
        String token = jsonString(loginResponse.getContentAsString(), "token");
        long unidadeId = this.unidadeId;

        var createUser = mockMvc.perform(post("/api/v1/usuarios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"unidadeId":%d,"nomeCompleto":"%s tecnico","email":"%s",
                                 "tipoAcesso":"TECNICO","cargo":"Técnico","senha":"Senha123",
                                 "dataNascimento":"1999-05-20"}
                                """.formatted(unidadeId, marker, marker + "-tecnico@" + dominioEmail)))
                .andReturn().getResponse();
        assertEquals(201, createUser.getStatus(), createUser.getContentAsString());
        long userId = jsonLong(createUser.getContentAsString(), "id");
        assertEquals(200, get("/api/v1/usuarios?unidadeId=" + unidadeId, token).getStatus());
        assertEquals(200, get("/api/v1/usuarios/" + userId, token).getStatus());

        var updateUser = mockMvc.perform(patch("/api/v1/usuarios/" + userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Técnico CRUD atualizado\"}"))
                .andReturn().getResponse();
        assertEquals(200, updateUser.getStatus(), updateUser.getContentAsString());
        assertEquals(200, mockMvc.perform(patch("/api/v1/usuarios/" + userId + "/ativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/usuarios/" + userId + "/desativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/usuarios/" + userId + "/ativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());

        var createLocal = mockMvc.perform(post("/api/v1/locais")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"unidadeId\":" + unidadeId + ",\"nome\":\"" + marker + " local\",\"tipoLocalEndereco\":\"SALA_AULA\",\"descricao\":\"Local de teste CRUD\"}"))
                .andReturn().getResponse();
        assertEquals(201, createLocal.getStatus(), createLocal.getContentAsString());
        long localId = jsonLong(createLocal.getContentAsString(), "id");
        assertEquals(200, get("/api/v1/locais?unidadeId=" + unidadeId, token).getStatus());
        assertEquals(200, get("/api/v1/locais/" + localId, token).getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/locais/" + localId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Local CRUD atualizado\"}"))
                .andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/locais/" + localId + "/desativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/locais/" + localId + "/ativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());

        var createEvent = mockMvc.perform(post("/api/v1/eventos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usuarioId":%d,"localEnderecoId":%d,"titulo":"Evento CRUD",
                                 "descricao":"Descrição","descricaoLocal":"Local teste","observacao":"",
                                 "dataHoraInicio":"2030-04-10T10:00:00","dataHoraFim":"2030-04-10T11:00:00"}
                                """.formatted(userId, localId)))
                .andReturn().getResponse();
        assertEquals(201, createEvent.getStatus(), createEvent.getContentAsString());
        long eventId = jsonLong(createEvent.getContentAsString(), "id");
        assertEquals(200, get("/api/v1/eventos", token).getStatus());
        assertEquals(200, get("/api/v1/eventos?usuarioId=" + userId, token).getStatus());
        assertEquals(200, get("/api/v1/eventos/" + eventId, token).getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/eventos/" + eventId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Evento CRUD atualizado\"}"))
                .andReturn().getResponse().getStatus());
        assertEquals(204, mockMvc.perform(delete("/api/v1/eventos/" + eventId)
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());

        var createAptidao = mockMvc.perform(post("/api/v1/aptidoes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":" + userId + ",\"categoriaProblema\":\"ELETRICA\",\"nota\":8.5}"))
                .andReturn().getResponse();
        assertEquals(201, createAptidao.getStatus(), createAptidao.getContentAsString());
        long aptitudeId = jsonLong(createAptidao.getContentAsString(), "id");
        assertEquals(200, get("/api/v1/aptidoes?usuarioId=" + userId, token).getStatus());
        assertEquals(200, get("/api/v1/aptidoes/" + aptitudeId, token).getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/aptidoes/" + aptitudeId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\":9.0}"))
                .andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/aptidoes/" + aptitudeId + "/desativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/aptidoes/" + aptitudeId + "/ativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());

        var createShift = mockMvc.perform(post("/api/v1/turnos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"unidadeId\":" + unidadeId + ",\"nome\":\"" + marker + " turno\",\"horaInicio\":28800,\"horaFim\":36000,\"atravessaMeiaNoite\":false}"))
                .andReturn().getResponse();
        assertEquals(201, createShift.getStatus(), createShift.getContentAsString());
        long shiftId = jsonLong(createShift.getContentAsString(), "id");
        assertEquals(200, get("/api/v1/turnos?unidadeId=" + unidadeId, token).getStatus());
        assertEquals(200, get("/api/v1/turnos/" + shiftId, token).getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/turnos/" + shiftId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Turno CRUD atualizado\"}"))
                .andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/turnos/" + shiftId + "/desativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/turnos/" + shiftId + "/ativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());

        var link = mockMvc.perform(post("/api/v1/turnos/" + shiftId + "/usuarios/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse();
        assertEquals(201, link.getStatus(), link.getContentAsString());
        long linkId = jsonLong(link.getContentAsString(), "id");
        assertEquals(200, get("/api/v1/turnos/" + shiftId + "/usuarios", token).getStatus());
        assertEquals(200, get("/api/v1/usuarios/" + userId + "/turnos", token).getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/turnos-usuarios/" + linkId + "/desativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/turnos-usuarios/" + linkId + "/ativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());

        assertEquals(200, get("/api/v1/perfil", token).getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/perfil")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Gestor CRUD atualizado\"}"))
                .andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(put("/api/v1/perfil/foto")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.test/foto-crud.png\"}"))
                .andReturn().getResponse().getStatus());
        assertEquals(200, get("/api/v1/perfil/foto", token).getStatus());
        assertEquals(204, mockMvc.perform(delete("/api/v1/perfil/foto")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
    }

    @Test
    void administradorCrudHttpEndpointsWorkAgainstPostgreSql() throws Exception {
        Usuario admin = new Usuario();
        admin.setUnidade(unidadeRepository.findById(unidadeId).orElseThrow());
        admin.setNomeCompleto("Administrador " + marker);
        admin.setEmail(marker + "-admin@" + dominioEmail);
        admin.setTipoAcesso(TipoAcesso.ADMINISTRADOR);
        admin.setCargo("Administrador");
        admin.setSenhaHash(passwordEncoder.encode("Senha123"));
        admin.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
        admin.setEstaAtivo(true);
        admin.setPrimeiroAcesso(false);
        usuarioRepository.save(admin);

        var loginResponse = login(admin.getEmail(), "Senha123", cnpj);
        assertEquals(200, loginResponse.getStatus(), loginResponse.getContentAsString());
        String token = jsonString(loginResponse.getContentAsString(), "token");

        var createInstitution = mockMvc.perform(post("/api/v1/instituicoes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"" + marker + " instituição\",\"tipoInstituicao\":\"ESCOLA\",\"dominioEmail\":\"" + marker + ".invalid\"}"))
                .andReturn().getResponse();
        assertEquals(201, createInstitution.getStatus(), createInstitution.getContentAsString());
        long institutionId = jsonLong(createInstitution.getContentAsString(), "id");
        assertEquals(200, get("/api/v1/instituicoes", token).getStatus());
        assertEquals(200, get("/api/v1/instituicoes/" + institutionId, token).getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/instituicoes/" + institutionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Instituição CRUD atualizada\"}"))
                .andReturn().getResponse().getStatus());

        var createUnit = mockMvc.perform(post("/api/v1/unidades")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"instituicaoId":%d,"nome":"Unidade CRUD","logradouro":"Rua Teste",
                                 "numero":"10","complemento":"","bairro":"Centro","cidade":"São Paulo",
                                 "estado":"SP","cep":"01001000","cnpj":"%s"}
                                """.formatted(institutionId, cnpjTemporario())))
                .andReturn().getResponse();
        assertEquals(201, createUnit.getStatus(), createUnit.getContentAsString());
        long unitId = jsonLong(createUnit.getContentAsString(), "id");
        assertEquals(200, get("/api/v1/unidades", token).getStatus());
        assertEquals(200, get("/api/v1/unidades/" + unitId, token).getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/unidades/" + unitId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Unidade CRUD atualizada\"}"))
                .andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/unidades/" + unitId + "/desativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/unidades/" + unitId + "/ativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());

        assertEquals(200, mockMvc.perform(patch("/api/v1/instituicoes/" + institutionId + "/desativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
        assertEquals(200, mockMvc.perform(patch("/api/v1/instituicoes/" + institutionId + "/ativar")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getStatus());
    }

    private String jsonString(String body, String name) {
        var matcher = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        assertTrue(matcher.find());
        return matcher.group(1);
    }

    private long jsonLong(String body, String name) {
        Matcher matcher = Pattern.compile("\\\"" + name + "\\\"\\s*:\\s*(\\d+)").matcher(body);
        assertTrue(matcher.find(), "Campo numérico ausente: " + name + " no JSON " + body);
        return Long.parseLong(matcher.group(1));
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        @Primary
        RefreshTokenRepository inMemoryRefreshTokenRepository() {
            return new RefreshTokenRepository() {
                private final ConcurrentMap<String, Entry> entries = new ConcurrentHashMap<>();

                @Override
                public void salvar(String token, Long usuarioId, Duration validade) {
                    entries.put(token, new Entry(usuarioId, Instant.now().plus(validade)));
                }

                @Override
                public Optional<Long> consumir(String token) {
                    Entry entry = entries.remove(token);
                    if (entry == null || entry.expiraEm().isBefore(Instant.now())) {
                        return Optional.empty();
                    }
                    return Optional.of(entry.usuarioId());
                }

                @Override
                public void remover(String token) {
                    entries.remove(token);
                }

                @Override
                public void removerTodosPorUsuarioId(Long usuarioId) {
                    entries.entrySet().removeIf(item -> item.getValue().usuarioId().equals(usuarioId));
                }

                record Entry(Long usuarioId, Instant expiraEm) {}
            };
        }
    }
}
