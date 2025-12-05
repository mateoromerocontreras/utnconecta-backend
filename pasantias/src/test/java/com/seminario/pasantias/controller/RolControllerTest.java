package com.seminario.pasantias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.RolRequest;
import com.seminario.pasantias.entity.Rol;
import com.seminario.pasantias.service.RolService;
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
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RolService rolService;

    private Rol rol;
    private RolRequest rolRequest;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombre("ESTUDIANTE");
        rol.setDescripcion("Rol de estudiante");

        rolRequest = new RolRequest();
        rolRequest.setId(1);
        rolRequest.setNombre("ESTUDIANTE");
        rolRequest.setDescripcion("Rol de estudiante");
    }

    @Test
    void registrarRol_Success() throws Exception {
        doNothing().when(rolService).createRol(any(Rol.class));

        mockMvc.perform(post("/roles/registrarRol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void registrarRol_MissingNombre() throws Exception {
        rolRequest.setNombre(null);

        mockMvc.perform(post("/roles/registrarRol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
    }

    @Test
    void registrarRol_EmptyNombre() throws Exception {
        rolRequest.setNombre("");

        mockMvc.perform(post("/roles/registrarRol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
    }

    @Test
    void registrarRol_ServiceException() throws Exception {
        doThrow(new RuntimeException("Error al crear rol")).when(rolService).createRol(any(Rol.class));

        mockMvc.perform(post("/roles/registrarRol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Error al crear rol"));
    }

    @Test
    void consultarRol_NoParams() throws Exception {
        List<Rol> roles = Arrays.asList(rol);
        when(rolService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/roles/consultarRol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("ESTUDIANTE"));
    }

    @Test
    void consultarRol_ByNombre() throws Exception {
        when(rolService.getRolByNombre("ESTUDIANTE")).thenReturn(Optional.of(rol));

        mockMvc.perform(get("/roles/consultarRol")
                        .param("nombre", "ESTUDIANTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("ESTUDIANTE"));
    }

    @Test
    void consultarRol_ByNombreNotFound() throws Exception {
        when(rolService.getRolByNombre("NO_EXISTE")).thenReturn(Optional.empty());

        mockMvc.perform(get("/roles/consultarRol")
                        .param("nombre", "NO_EXISTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void modificarRol_Success() throws Exception {
        doNothing().when(rolService).updateRol(any(Rol.class));

        mockMvc.perform(post("/roles/modificarRol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void modificarRol_MissingId() throws Exception {
        rolRequest.setId(null);

        mockMvc.perform(post("/roles/modificarRol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El id y el nombre son obligatorios"));
    }

    @Test
    void modificarRol_MissingNombre() throws Exception {
        rolRequest.setNombre(null);

        mockMvc.perform(post("/roles/modificarRol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El id y el nombre son obligatorios"));
    }

    @Test
    void eliminarRol_Success() throws Exception {
        RolRequest deleteRequest = new RolRequest();
        deleteRequest.setNombre("ESTUDIANTE");
        doNothing().when(rolService).deleteRolByNombre("ESTUDIANTE");

        mockMvc.perform(post("/roles/eliminarRol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void eliminarRol_MissingNombre() throws Exception {
        RolRequest deleteRequest = new RolRequest();
        deleteRequest.setNombre(null);

        mockMvc.perform(post("/roles/eliminarRol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
    }

    @Test
    void getAllRoles_Success() throws Exception {
        List<Rol> roles = Arrays.asList(rol);
        when(rolService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("ESTUDIANTE"))
                .andExpect(jsonPath("$[0].idRol").value(1));
    }

    @Test
    void getRolById_Success() throws Exception {
        when(rolService.getRolById(1)).thenReturn(Optional.of(rol));

        mockMvc.perform(get("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.idRol").value(1));
    }

    @Test
    void getRolById_NotFound() throws Exception {
        when(rolService.getRolById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/roles/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}

