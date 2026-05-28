package com.seminario.pasantias;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.EstudianteRegisterRequest;
import com.seminario.pasantias.dto.LoginRequest;
import com.seminario.pasantias.dto.RegisterRequest;
import com.seminario.pasantias.entity.Empresa;
import com.seminario.pasantias.entity.Estudiante;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.persistence.EmpresaMapper;
import com.seminario.pasantias.persistence.EstudianteMapper;
import com.seminario.pasantias.persistence.UsuarioMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(
        scripts = "classpath:sql/schema.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
class AuthAndEmpresaRegistrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Autowired
    private EmpresaMapper empresaMapper;

    @Test
    void studentCanCreateAccount_shouldCreateInactiveUsuarioAndEstudianteProfile() throws Exception {
        String unique = String.valueOf(Instant.now().toEpochMilli());
        String email = "it.student." + unique + "@example.com";

        EstudianteRegisterRequest request = new EstudianteRegisterRequest(
                "Juan",
                "Pérez",
                "40123456",
                "3512345678",
                email,
                "password1"
        );

        mockMvc.perform(
                        post("/auth/registrarEstudiante")
                                .contentType("application/json")
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isEmpty())
                .andExpect(jsonPath("$.username").value(email))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.message").value("Registro exitoso. Revisa tu correo para confirmar la cuenta."));

        Optional<Usuario> createdUsuario = usuarioMapper.findByEmail(email);
        assertThat(createdUsuario).isPresent();
        assertThat(createdUsuario.orElseThrow().getActivo()).isFalse();

        Optional<Estudiante> createdEstudiante = estudianteMapper.findByEmail(email);
        assertThat(createdEstudiante).isPresent();
        assertThat(createdEstudiante.orElseThrow().getIdUsuario()).isEqualTo(createdUsuario.orElseThrow().getIdUsuario());
    }

    @Test
    void empresaCanCreateAccount_shouldRegisterUsuarioLoginAndCreateEmpresa() throws Exception {
        String unique = String.valueOf(Instant.now().toEpochMilli());
        String username = "it_empresa_" + unique;
        String email = "it.empresa." + unique + "@example.com";
        String password = "password1";

        RegisterRequest register = new RegisterRequest(username, email, password, "EMPRESA");

        mockMvc.perform(
                        post("/usuarios/registrarUsuario")
                                .contentType("application/json")
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(register)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        Usuario created = usuarioMapper.findByEmail(email).orElseThrow();
        assertThat(created.getActivo()).isTrue();

        LoginRequest login = new LoginRequest(email, password);
        String token = extractToken(
                mockMvc.perform(
                                post("/auth/iniciarSesion")
                                        .contentType("application/json")
                                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(login)))
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.token").isString())
                        .andExpect(jsonPath("$.rol").value("EMPRESA"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        );
        assertThat(token).isNotBlank();

        String payload = """
                {
                  "nombre": "Empresa IT %s",
                  "razonSocial": "Empresa IT %s S.A.",
                  "cuit": "30-%s-7",
                  "ciudad": "Córdoba",
                  "calle": "San Martín",
                  "nroCalle": 123,
                  "piso": null,
                  "departamento": null,
                  "barrio": "Centro",
                  "email": "%s",
                  "contacto": [
                    {
                      "nombre": "Ana",
                      "apellido": "García",
                      "emailResponsable": "contacto.%s@example.com",
                      "telefonoResponsable": "3511111111"
                    }
                  ]
                }
                """.formatted(unique, unique, unique.substring(Math.max(0, unique.length() - 8)), email, unique);

        mockMvc.perform(
                        post("/empresas/crearEmpresa")
                                .header("Authorization", "Bearer " + token)
                                .contentType("application/json")
                                .content(Objects.requireNonNull(payload))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        Empresa empresa = empresaMapper.findByIdUsuario(created.getIdUsuario());
        assertThat(empresa).isNotNull();
        assertThat(empresa.getNombre()).contains("Empresa IT");
        assertThat(empresa.getCuit()).isNotBlank();
    }

    private String extractToken(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode tokenNode = root.path("token");
        return tokenNode.isTextual() ? tokenNode.asText() : null;
    }
}

