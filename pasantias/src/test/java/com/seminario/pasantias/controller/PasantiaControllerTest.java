package com.seminario.pasantias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.request.ActualizarEstadoPasantiaDTO;
import com.seminario.pasantias.dto.request.PasantiaRequestDTO;
import com.seminario.pasantias.dto.response.PasantiaDetalleDTO;
import com.seminario.pasantias.dto.response.PasantiaResponseDTO;
import com.seminario.pasantias.entity.EstadoPasantia;
import com.seminario.pasantias.security.SecurityService;
import com.seminario.pasantias.service.PasantiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PasantiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PasantiaService pasantiaService;

    @MockBean
    private SecurityService securityService;

    private PasantiaRequestDTO pasantiaRequest;
    private PasantiaResponseDTO pasantiaResponse;
    private PasantiaDetalleDTO pasantiaDetalle;

    @BeforeEach
    void setUp() {
        pasantiaRequest = new PasantiaRequestDTO();
        pasantiaRequest.setTitulo("Pasantía en Desarrollo Backend");
        pasantiaRequest.setPuestoACubrir("Desarrollador Java Jr");
        pasantiaRequest.setCiudad("Córdoba");
        pasantiaRequest.setModalidad("Híbrida");
        pasantiaRequest.setAsignacionEstimulo(50000.0f);
        pasantiaRequest.setCantidadDePasantes(2);
        pasantiaRequest.setFechaPublicacion(LocalDate.now().plusDays(1));
        pasantiaRequest.setFechaCaducidad(LocalDate.now().plusMonths(2));
        pasantiaRequest.setIdEmpresa(1);
        pasantiaRequest.setIdsCarreras(Arrays.asList(1, 2));
        pasantiaRequest.setEmailContacto("rrhh@empresa.com");

        pasantiaResponse = new PasantiaResponseDTO();
        pasantiaResponse.setIdPasantia(1);
        pasantiaResponse.setTitulo("Pasantía en Desarrollo Backend");
        pasantiaResponse.setEstado(EstadoPasantia.PENDIENTE_DE_APROBACION);

        pasantiaDetalle = new PasantiaDetalleDTO();
        pasantiaDetalle.setIdPasantia(1);
        pasantiaDetalle.setTitulo("Pasantía en Desarrollo Backend");
    }

    @Test
    void getAllPasantias_Success() throws Exception {
        List<PasantiaResponseDTO> pasantias = Arrays.asList(pasantiaResponse);
        when(pasantiaService.obtenerTodasLasPasantias()).thenReturn(pasantias);

        mockMvc.perform(get("/pasantias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPasantia").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Pasantía en Desarrollo Backend"));
    }

    @Test
    void getPasantiasPublicadas_Success() throws Exception {
        pasantiaResponse.setEstado(EstadoPasantia.PUBLICADA);
        List<PasantiaResponseDTO> pasantias = Arrays.asList(pasantiaResponse);
        when(pasantiaService.obtenerPasantiasPublicadas()).thenReturn(pasantias);

        mockMvc.perform(get("/pasantias/publicadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PUBLICADA"));
    }

    @Test
    void getPasantiaById_Success() throws Exception {
        when(pasantiaService.obtenerPasantiaPorId(1)).thenReturn(pasantiaDetalle);

        mockMvc.perform(get("/pasantias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPasantia").value(1))
                .andExpect(jsonPath("$.titulo").value("Pasantía en Desarrollo Backend"));
    }

    @Test
    void getPasantiaById_NotFound() throws Exception {
        when(pasantiaService.obtenerPasantiaPorId(999))
                .thenThrow(new IllegalArgumentException("Pasantía no encontrada"));

        mockMvc.perform(get("/pasantias/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(-1))
                .andExpect(jsonPath("$.mensaje").value("Pasantía no encontrada"));
    }

    @Test
    @WithMockUser(roles = {"EMPRESA"})
    void registrarPasantia_Success() throws Exception {
        doNothing().when(securityService).validarPermisoCrearPasantia(1);
        when(pasantiaService.crearPasantia(any(PasantiaRequestDTO.class))).thenReturn(pasantiaResponse);

        mockMvc.perform(post("/pasantias/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pasantiaRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Pasantía registrada exitosamente"))
                .andExpect(jsonPath("$.data.idPasantia").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void registrarPasantia_AsAdministrator() throws Exception {
        doNothing().when(securityService).validarPermisoCrearPasantia(1);
        when(pasantiaService.crearPasantia(any(PasantiaRequestDTO.class))).thenReturn(pasantiaResponse);

        mockMvc.perform(post("/pasantias/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pasantiaRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(0));
    }

    @Test
    @WithMockUser(roles = {"EMPRESA"})
    void registrarPasantia_Unauthorized() throws Exception {
        doThrow(new SecurityException("No tienes permiso")).when(securityService)
                .validarPermisoCrearPasantia(1);

        mockMvc.perform(post("/pasantias/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pasantiaRequest))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value(-1))
                .andExpect(jsonPath("$.mensaje").value("No tienes permiso"));
    }

    @Test
    @WithMockUser(roles = {"EMPRESA"})
    void registrarPasantia_InvalidData() throws Exception {
        pasantiaRequest.setTitulo(""); // Invalid: too short
        doNothing().when(securityService).validarPermisoCrearPasantia(1);

        mockMvc.perform(post("/pasantias/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pasantiaRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void aprobarPasantia_Success() throws Exception {
        pasantiaResponse.setEstado(EstadoPasantia.PUBLICADA);
        doNothing().when(securityService).validarEsAdministrador();
        when(pasantiaService.actualizarEstado(eq(1), any(ActualizarEstadoPasantiaDTO.class)))
                .thenReturn(pasantiaResponse);

        mockMvc.perform(put("/pasantias/1/aprobar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Pasantía aprobada y publicada exitosamente"))
                .andExpect(jsonPath("$.data.estado").value("PUBLICADA"));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void aprobarPasantia_Unauthorized() throws Exception {
        doThrow(new SecurityException("Solo administradores")).when(securityService)
                .validarEsAdministrador();

        mockMvc.perform(put("/pasantias/1/aprobar")
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value(-1));
    }

    @Test
    @WithMockUser(roles = {"EMPRESA"})
    void actualizarPasantia_Success() throws Exception {
        doNothing().when(securityService).validarPermisoModificarPasantia(1);
        when(pasantiaService.actualizarPasantia(eq(1), any(PasantiaRequestDTO.class)))
                .thenReturn(pasantiaResponse);

        mockMvc.perform(put("/pasantias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pasantiaRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Pasantía actualizada exitosamente"));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void finalizarPasantia_Success() throws Exception {
        pasantiaResponse.setEstado(EstadoPasantia.FINALIZADA);
        doNothing().when(securityService).validarEsAdministrador();
        when(pasantiaService.actualizarEstado(eq(1), any(ActualizarEstadoPasantiaDTO.class)))
                .thenReturn(pasantiaResponse);

        mockMvc.perform(put("/pasantias/1/finalizar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Pasantía finalizada exitosamente"))
                .andExpect(jsonPath("$.data.estado").value("FINALIZADA"));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void finalizarPasantia_Unauthorized() throws Exception {
        doThrow(new SecurityException("Solo administradores")).when(securityService)
                .validarEsAdministrador();

        mockMvc.perform(put("/pasantias/1/finalizar")
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value(-1));
    }
}

