package com.seminario.pasantias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.EstudianteBasicResponse;
import com.seminario.pasantias.dto.EstudianteRegisterRequest;
import com.seminario.pasantias.dto.EstudianteUpdateProfileRequest;
import com.seminario.pasantias.dto.EstudianteUpdateRequest;
import com.seminario.pasantias.entity.Estudiante;
import com.seminario.pasantias.entity.Rol;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.service.EmailVerificationService;
import com.seminario.pasantias.service.EstudianteService;
import com.seminario.pasantias.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EstudianteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EstudianteService estudianteService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    private Usuario usuario;
    private Estudiante estudiante;
    private EstudianteRegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setIdRol(1);
        rol.setNombre("ESTUDIANTE");

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setUsername("test@example.com");
        usuario.setEmail("test@example.com");
        usuario.setRol(rol);

        estudiante = new Estudiante();
        estudiante.setIdEstudiante(1);
        estudiante.setNombre("Juan");
        estudiante.setApellido("Perez");
        estudiante.setDni("12345678");
        estudiante.setEmail("test@example.com");

        registerRequest = new EstudianteRegisterRequest();
        registerRequest.setNombre("Juan");
        registerRequest.setApellido("Perez");
        registerRequest.setDni("12345678");
        registerRequest.setTelCelular("1234567890");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
    }

    @Test
    void crearEstudiante_Success() throws Exception {
        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(usuarioService.createUsuario(anyString(), anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn(usuario);
        when(estudianteService.createEstudianteBasico(anyString(), anyString(), anyString(),
                anyString(), anyString(), anyInt())).thenReturn(estudiante);
        doNothing().when(emailVerificationService).enviarCorreoDeVerificacion(any(Usuario.class));

        mockMvc.perform(post("/estudiantes/crearEstudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Se envio un email de verificacion para activar la cuenta"));
    }

    @Test
    void crearEstudiante_MissingNombre() throws Exception {
        registerRequest.setNombre(null);

        mockMvc.perform(post("/estudiantes/crearEstudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
    }

    @Test
    void crearEstudiante_EmailAlreadyExists() throws Exception {
        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/estudiantes/crearEstudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El email ya esta registrado"));
    }

    @Test
    void crearEstudiante_InvalidPassword() throws Exception {
        registerRequest.setPassword("short");
        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/estudiantes/crearEstudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("La contrasena debe tener al menos 8 caracteres, una letra minuscula y un numero"));
    }

    @Test
    void completarPerfil_Success() throws Exception {
        EstudianteUpdateRequest updateRequest = new EstudianteUpdateRequest();
        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        doNothing().when(estudianteService).updateEstudiante(anyInt(), any(EstudianteUpdateRequest.class));

        mockMvc.perform(post("/estudiantes/completarPerfil")
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Perfil completado exitosamente"));
    }

    @Test
    void completarPerfil_UserNotFound() throws Exception {
        EstudianteUpdateRequest updateRequest = new EstudianteUpdateRequest();
        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/estudiantes/completarPerfil")
                        .param("email", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }

    @Test
    void obtenerPerfil_Success() throws Exception {
        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(estudianteService.findByUsuarioId(1)).thenReturn(Optional.of(estudiante));

        mockMvc.perform(get("/estudiantes/perfil")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    @Test
    void obtenerPerfil_UserNotFound() throws Exception {
        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/estudiantes/perfil")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    void getEstudiantes_All() throws Exception {
        EstudianteBasicResponse response = new EstudianteBasicResponse();
        response.setNombre("Juan");
        response.setApellido("Perez");
        List<EstudianteBasicResponse> estudiantes = Arrays.asList(response);
        when(estudianteService.getAllEstudiantesBasic()).thenReturn(estudiantes);

        mockMvc.perform(get("/estudiantes/getEstudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    void getEstudiantes_ByNombre() throws Exception {
        EstudianteBasicResponse response = new EstudianteBasicResponse();
        response.setNombre("Juan");
        response.setApellido("Perez");
        when(estudianteService.getEstudianteByNombreBasic("Juan")).thenReturn(Optional.of(response));

        mockMvc.perform(get("/estudiantes/getEstudiantes")
                        .param("nombre", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void updateEstudiante_Success() throws Exception {
        EstudianteUpdateProfileRequest updateRequest = new EstudianteUpdateProfileRequest();
        updateRequest.setEmail("newemail@example.com");
        doNothing().when(estudianteService).updateEstudianteProfile(anyString(), any(EstudianteUpdateProfileRequest.class));

        mockMvc.perform(put("/estudiantes/updateEstudiante")
                        .param("currentEmail", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Perfil actualizado exitosamente"));
    }

    @Test
    void updateEstudiante_MissingCurrentEmail() throws Exception {
        EstudianteUpdateProfileRequest updateRequest = new EstudianteUpdateProfileRequest();

        mockMvc.perform(put("/estudiantes/updateEstudiante")
                        .param("currentEmail", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El email actual es obligatorio"));
    }

    @Test
    void updateEstudiante_InvalidEmailFormat() throws Exception {
        EstudianteUpdateProfileRequest updateRequest = new EstudianteUpdateProfileRequest();
        updateRequest.setEmail("invalid-email");

        mockMvc.perform(put("/estudiantes/updateEstudiante")
                        .param("currentEmail", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El formato del email es inválido"));
    }
}

