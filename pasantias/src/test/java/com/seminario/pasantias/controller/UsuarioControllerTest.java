package com.seminario.pasantias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.EliminarUsuarioRequest;
import com.seminario.pasantias.dto.RegisterRequest;
import com.seminario.pasantias.dto.UpdateUsuarioRequest;
import com.seminario.pasantias.entity.Usuario;
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

/**
 * Test de integración para {@link UsuarioController}.
 * 
 * <p>Esta clase contiene tests de integración que verifican el comportamiento
 * de los endpoints del controlador de usuarios. Los tests utilizan:
 * <ul>
 *   <li>Spring Boot Test Context completo</li>
 *   <li>H2 base de datos en memoria para pruebas</li>
 *   <li>MockMvc para simular peticiones HTTP</li>
 *   <li>MockBean para simular el servicio UsuarioService</li>
 * </ul>
 * 
 * <p>Los tests cubren los siguientes escenarios:
 * <ul>
 *   <li>Registro de usuarios (casos exitosos y validaciones)</li>
 *   <li>Consulta de usuarios (por nombre y listado completo)</li>
 *   <li>Actualización de usuarios</li>
 *   <li>Desactivación de usuarios</li>
 *   <li>Eliminación de usuarios</li>
 * </ul>
 * 
 * <p><strong>Nota:</strong> Estos son tests de integración que prueban la capa
 * HTTP completa, incluyendo validaciones del controlador, serialización JSON
 * y manejo de respuestas. El servicio UsuarioService está mockeado para aislar
 * las pruebas del controlador.
 * 
 * @author Sistema de Pasantías
 * @version 1.0
 * @see UsuarioController
 * @see UsuarioService
 * @since 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    /** Usuario de prueba utilizado en los tests */
    private Usuario usuario;
    
    /** Request de registro utilizado en los tests */
    private RegisterRequest registerRequest;

    /**
     * Configuración inicial antes de cada test.
     * 
     * <p>Inicializa los objetos de prueba necesarios:
     * <ul>
     *   <li>Usuario de prueba con datos básicos</li>
     *   <li>RegisterRequest con datos válidos para registro</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRol("ESTUDIANTE");
    }

    /**
     * Test para verificar el registro exitoso de un usuario.
     * 
     * <p>Verifica que cuando se envía una petición POST con datos válidos
     * al endpoint {@code /usuarios/registrarUsuario}, el sistema:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta 0 (éxito)</li>
     *   <li>No incluye mensaje de error</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void registrarUsuario_Success() throws Exception {
        when(usuarioService.createUsuario(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(usuario);

        mockMvc.perform(post("/usuarios/registrarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    /**
     * Test para verificar la validación cuando falta el username en el registro.
     * 
     * <p>Verifica que cuando se intenta registrar un usuario sin username:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando que el username es obligatorio</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void registrarUsuario_MissingUsername() throws Exception {
        registerRequest.setUsername(null);

        mockMvc.perform(post("/usuarios/registrarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El username es obligatorio"));
    }

    /**
     * Test para verificar la validación cuando falta el email en el registro.
     * 
     * <p>Verifica que cuando se intenta registrar un usuario sin email:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando que el email es obligatorio</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void registrarUsuario_MissingEmail() throws Exception {
        registerRequest.setEmail(null);

        mockMvc.perform(post("/usuarios/registrarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El email es obligatorio"));
    }

    /**
     * Test para verificar la validación cuando falta la contraseña en el registro.
     * 
     * <p>Verifica que cuando se intenta registrar un usuario sin contraseña:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando que la contraseña es obligatoria</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void registrarUsuario_MissingPassword() throws Exception {
        registerRequest.setPassword(null);

        mockMvc.perform(post("/usuarios/registrarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("La contraseña es obligatoria"));
    }

    /**
     * Test para verificar la validación de formato de contraseña.
     * 
     * <p>Verifica que cuando se intenta registrar un usuario con una contraseña
     * que no cumple los requisitos (mínimo 8 caracteres, una letra minúscula y un número):
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando los requisitos de la contraseña</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void registrarUsuario_InvalidPassword() throws Exception {
        registerRequest.setPassword("short");

        mockMvc.perform(post("/usuarios/registrarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("La contraseña debe tener al menos 8 caracteres, una letra minúscula y un número"));
    }

    /**
     * Test para verificar la validación cuando falta el rol en el registro.
     * 
     * <p>Verifica que cuando se intenta registrar un usuario sin rol:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando que el rol es obligatorio</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void registrarUsuario_MissingRol() throws Exception {
        registerRequest.setRol(null);

        mockMvc.perform(post("/usuarios/registrarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El rol es obligatorio"));
    }

    /**
     * Test para verificar el manejo de excepciones del servicio durante el registro.
     * 
     * <p>Verifica que cuando el servicio lanza una excepción durante el registro:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye el mensaje de error de la excepción</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void registrarUsuario_ServiceException() throws Exception {
        doThrow(new RuntimeException("Error al crear usuario")).when(usuarioService)
                .createUsuario(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(post("/usuarios/registrarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Error al crear usuario"));
    }

    /**
     * Test para verificar la consulta de todos los usuarios activos.
     * 
     * <p>Verifica que cuando se realiza una petición GET sin parámetros
     * al endpoint {@code /usuarios/consultarUsuario}:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna una lista con todos los usuarios activos</li>
     *   <li>Los usuarios incluyen username y email</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void consultarUsuario_NoParams() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioService.findAllActive()).thenReturn(usuarios);

        mockMvc.perform(get("/usuarios/consultarUsuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    /**
     * Test para verificar la consulta de un usuario específico por nombre.
     * 
     * <p>Verifica que cuando se realiza una petición GET con el parámetro
     * {@code nombre} al endpoint {@code /usuarios/consultarUsuario}:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna el usuario encontrado con sus datos</li>
     *   <li>El usuario incluye username y email correctos</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void consultarUsuario_ByNombre() throws Exception {
        when(usuarioService.findByUsername("testuser")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/usuarios/consultarUsuario")
                        .param("nombre", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    /**
     * Test para verificar el comportamiento cuando no se encuentra un usuario por nombre.
     * 
     * <p>Verifica que cuando se consulta un usuario que no existe:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna un objeto vacío o null</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void consultarUsuario_ByNombreNotFound() throws Exception {
        when(usuarioService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/consultarUsuario")
                        .param("nombre", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    /**
     * Test para verificar la actualización exitosa de un usuario.
     * 
     * <p>Verifica que cuando se envía una petición POST con datos válidos
     * al endpoint {@code /usuarios/actualizarUsuario}:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta 0 (éxito)</li>
     *   <li>No incluye mensaje de error</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void actualizarUsuario_Success() throws Exception {
        UpdateUsuarioRequest updateRequest = new UpdateUsuarioRequest();
        updateRequest.setIdUsuario("testuser");
        updateRequest.setEmail("newemail@example.com");
        doNothing().when(usuarioService).updateUsuario(any(UpdateUsuarioRequest.class));

        mockMvc.perform(post("/usuarios/actualizarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    /**
     * Test para verificar la validación cuando faltan idUsuario y nombre en la actualización.
     * 
     * <p>Verifica que cuando se intenta actualizar un usuario sin proporcionar
     * idUsuario ni nombre:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando que debe proporcionar al menos uno de los campos</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void actualizarUsuario_MissingIdAndNombre() throws Exception {
        UpdateUsuarioRequest updateRequest = new UpdateUsuarioRequest();

        mockMvc.perform(post("/usuarios/actualizarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Debe proporcionar al menos idUsuario o nombre"));
    }

    /**
     * Test para verificar la validación de formato de contraseña en la actualización.
     * 
     * <p>Verifica que cuando se intenta actualizar un usuario con una contraseña
     * que no cumple los requisitos (mínimo 8 caracteres, una letra minúscula,
     * una mayúscula y un número):
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando los requisitos de la contraseña</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void actualizarUsuario_InvalidPassword() throws Exception {
        UpdateUsuarioRequest updateRequest = new UpdateUsuarioRequest();
        updateRequest.setIdUsuario("testuser");
        updateRequest.setPassword("short");

        mockMvc.perform(post("/usuarios/actualizarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("La contraseña debe tener al menos 8 caracteres, 1 minúscula, 1 mayúscula y 1 número"));
    }

    /**
     * Test para verificar la desactivación exitosa de un usuario.
     * 
     * <p>Verifica que cuando se envía una petición POST con un idUsuario válido
     * al endpoint {@code /usuarios/desactivarUsuario}:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta 0 (éxito)</li>
     *   <li>El usuario es encontrado y desactivado correctamente</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void desactivarUsuario_Success() throws Exception {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest();
        request.setIdUsuario("testuser");
        when(usuarioService.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).deactivateUsuario(1);

        mockMvc.perform(post("/usuarios/desactivarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * Test para verificar la validación cuando falta el idUsuario en la desactivación.
     * 
     * <p>Verifica que cuando se intenta desactivar un usuario sin proporcionar idUsuario:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando que el idUsuario es obligatorio</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void desactivarUsuario_MissingIdUsuario() throws Exception {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest();

        mockMvc.perform(post("/usuarios/desactivarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Debe proporcionar el idUsuario (username) del usuario a desactivar."));
    }

    /**
     * Test para verificar el comportamiento cuando no se encuentra el usuario a desactivar.
     * 
     * <p>Verifica que cuando se intenta desactivar un usuario que no existe:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando que el usuario no fue encontrado</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void desactivarUsuario_NotFound() throws Exception {
        UpdateUsuarioRequest request = new UpdateUsuarioRequest();
        request.setIdUsuario("nonexistent");
        when(usuarioService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(post("/usuarios/desactivarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado."));
    }

    /**
     * Test para verificar la eliminación exitosa de un usuario.
     * 
     * <p>Verifica que cuando se envía una petición POST con un nombre válido
     * al endpoint {@code /usuarios/eliminarUsuario}:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta 0 (éxito)</li>
     *   <li>No incluye mensaje de error</li>
     *   <li>El usuario es encontrado y eliminado correctamente</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void eliminarUsuario_Success() throws Exception {
        EliminarUsuarioRequest request = new EliminarUsuarioRequest();
        request.setNombre("testuser");
        when(usuarioService.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).deleteUsuarioByNombre("testuser");

        mockMvc.perform(post("/usuarios/eliminarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    /**
     * Test para verificar la validación cuando falta el nombre en la eliminación.
     * 
     * <p>Verifica que cuando se intenta eliminar un usuario sin proporcionar nombre:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando que el nombre es obligatorio</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void eliminarUsuario_MissingNombre() throws Exception {
        EliminarUsuarioRequest request = new EliminarUsuarioRequest();

        mockMvc.perform(post("/usuarios/eliminarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
    }

    /**
     * Test para verificar el comportamiento cuando no se encuentra el usuario a eliminar.
     * 
     * <p>Verifica que cuando se intenta eliminar un usuario que no existe:
     * <ul>
     *   <li>Retorna código HTTP 200 (OK)</li>
     *   <li>Retorna código de respuesta -1 (error)</li>
     *   <li>Incluye mensaje indicando que el usuario no fue encontrado</li>
     * </ul>
     * 
     * @throws Exception si ocurre un error durante la ejecución del test
     */
    @Test
    void eliminarUsuario_NotFound() throws Exception {
        EliminarUsuarioRequest request = new EliminarUsuarioRequest();
        request.setNombre("nonexistent");
        when(usuarioService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(post("/usuarios/eliminarUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }
}

