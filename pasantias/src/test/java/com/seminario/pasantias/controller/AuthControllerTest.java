package com.seminario.pasantias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.AuthResponse;
import com.seminario.pasantias.dto.EstudianteRegisterRequest;
import com.seminario.pasantias.dto.LoginRequest;
import com.seminario.pasantias.entity.Rol;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.security.JwtUtil;
import com.seminario.pasantias.service.EmailVerificationService;
import com.seminario.pasantias.service.EstudianteService;
import com.seminario.pasantias.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private EstudianteService estudianteService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    @MockBean
    private JwtUtil jwtUtil;

    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombre("ESTUDIANTE");

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setUsername("test@example.com");
        usuario.setEmail("test@example.com");
        usuario.setPassword("$2a$10$hashedpassword");
        usuario.setActivo(true);
        usuario.setRol(rol);
    }

    @Test
    void iniciarSesion_Success() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(usuarioService.verifyPassword("password123", usuario.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/auth/iniciarSesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"));
    }

    @Test
    void iniciarSesion_EmptyEmail() throws Exception {
        LoginRequest request = new LoginRequest("", "password123");

        mockMvc.perform(post("/auth/iniciarSesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El email es obligatorio"));
    }

    @Test
    void iniciarSesion_EmptyPassword() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "");

        mockMvc.perform(post("/auth/iniciarSesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La contraseña es obligatoria"));
    }

    @Test
    void iniciarSesion_UserNotFound() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/iniciarSesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void iniciarSesion_InvalidPassword() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "wrongpassword");

        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(usuarioService.verifyPassword("wrongpassword", usuario.getPassword())).thenReturn(false);

        mockMvc.perform(post("/auth/iniciarSesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void iniciarSesion_UserNotActive() throws Exception {
        usuario.setActivo(false);
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        when(usuarioService.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(usuarioService.verifyPassword("password123", usuario.getPassword())).thenReturn(true);

        mockMvc.perform(post("/auth/iniciarSesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El usuario aun no confirmo su email"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getCurrentUser_Success() throws Exception {
        when(usuarioService.findByUsername("test@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"));
    }

    @Test
    @WithMockUser(username = "nonexistent")
    void getCurrentUser_NotFound() throws Exception {
        when(usuarioService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }

    @Test
    void cerrarSesion_Success() throws Exception {
        mockMvc.perform(post("/auth/cerrarSesion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void registrarEstudiante_Success() throws Exception {
        EstudianteRegisterRequest request = new EstudianteRegisterRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setDni("12345678");
        request.setTelCelular("1234567890");
        request.setEmail("juan@example.com");
        request.setPassword("password123");

        when(usuarioService.findByEmail("juan@example.com")).thenReturn(Optional.empty());
        when(usuarioService.createUsuario(anyString(), anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn(usuario);
        com.seminario.pasantias.entity.Estudiante estudiante = new com.seminario.pasantias.entity.Estudiante();
        estudiante.setIdEstudiante(1);
        when(estudianteService.createEstudianteBasico(anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyInt())).thenReturn(estudiante);
        // enviarCorreoDeVerificacion is void, so doNothing() is correct
        doNothing().when(emailVerificationService).enviarCorreoDeVerificacion(any(Usuario.class));

        mockMvc.perform(post("/auth/registrarEstudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.rol").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.message").value("Registro exitoso. Revisa tu correo para confirmar la cuenta."));
    }

    @Test
    void registrarEstudiante_EmailAlreadyExists() throws Exception {
        EstudianteRegisterRequest request = new EstudianteRegisterRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setDni("12345678");
        request.setTelCelular("1234567890");
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        when(usuarioService.findByEmail("existing@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/auth/registrarEstudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El email ya esta registrado"));
    }

    @Test
    void registrarEstudiante_InvalidPassword() throws Exception {
        EstudianteRegisterRequest request = new EstudianteRegisterRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setDni("12345678");
        request.setTelCelular("1234567890");
        request.setEmail("juan@example.com");
        request.setPassword("short"); // Invalid password

        when(usuarioService.findByEmail("juan@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/registrarEstudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La contrasena debe tener al menos 8 caracteres, una letra minuscula y un numero"));
    }

    @Test
    void confirmarEmail_Success() throws Exception {
        String token = "valid-token";
        doNothing().when(emailVerificationService).confirmarToken(token);

        mockMvc.perform(get("/auth/confirmar")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Cuenta verificada correctamente. Ya podes iniciar sesion."));
    }

    @Test
    void confirmarEmail_InvalidToken() throws Exception {
        String token = "invalid-token";
        doThrow(new RuntimeException("Token inválido")).when(emailVerificationService).confirmarToken(token);

        mockMvc.perform(get("/auth/confirmar")
                        .param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("No se pudo verificar la cuenta: Token inválido"));
    }
}

