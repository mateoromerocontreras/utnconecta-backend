package com.seminario.pasantias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.request.PostulacionRequestDTO;
import com.seminario.pasantias.dto.response.PostulacionDetalleDTO;
import com.seminario.pasantias.dto.response.PostulacionResponseDTO;
import com.seminario.pasantias.entity.EstadoPostulacion;
import com.seminario.pasantias.service.PostulacionService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PostulacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostulacionService postulacionService;

    private PostulacionRequestDTO postulacionRequest;
    private PostulacionResponseDTO postulacionResponse;
    private PostulacionDetalleDTO postulacionDetalle;

    @BeforeEach
    void setUp() {
        postulacionRequest = new PostulacionRequestDTO();
        postulacionRequest.setFechaPostulacion(LocalDate.now());
        postulacionRequest.setIdPasantia(1);
        postulacionRequest.setIdEstudiante(1);
        postulacionRequest.setEstado(EstadoPostulacion.BORRADOR);

        postulacionResponse = new PostulacionResponseDTO();
        postulacionResponse.setIdPostulacion(1);
        postulacionResponse.setIdPasantia(1);
        postulacionResponse.setIdEstudiante(1);
        postulacionResponse.setEstado(EstadoPostulacion.BORRADOR);
        postulacionResponse.setNombreEstudiante("Juan");
        postulacionResponse.setApellidoEstudiante("Perez");
        postulacionResponse.setTituloPasantia("Pasantía en Desarrollo");

        postulacionDetalle = PostulacionDetalleDTO.builder()
                .idPostulacion(1)
                .build();
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void registrarPostulacion_Success() throws Exception {
        when(postulacionService.crearPostulacion(any(PostulacionRequestDTO.class)))
                .thenReturn(postulacionResponse);

        mockMvc.perform(post("/postulaciones/registrarPostulacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postulacionRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Postulación registrada exitosamente"))
                .andExpect(jsonPath("$.data.idPostulacion").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void registrarPostulacion_AsAdministrator() throws Exception {
        when(postulacionService.crearPostulacion(any(PostulacionRequestDTO.class)))
                .thenReturn(postulacionResponse);

        mockMvc.perform(post("/postulaciones/registrarPostulacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postulacionRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(0));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void registrarPostulacion_ServiceException() throws Exception {
        when(postulacionService.crearPostulacion(any(PostulacionRequestDTO.class)))
                .thenThrow(new RuntimeException("Error al crear postulación"));

        mockMvc.perform(post("/postulaciones/registrarPostulacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postulacionRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value(-1))
                .andExpect(jsonPath("$.mensaje").value("Error al crear postulación"));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void registrarPostulacion_InvalidData() throws Exception {
        postulacionRequest.setIdPasantia(null); // Invalid: required field

        mockMvc.perform(post("/postulaciones/registrarPostulacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postulacionRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void consultarPostulaciones_Success() throws Exception {
        List<PostulacionResponseDTO> postulaciones = Arrays.asList(postulacionResponse);
        when(postulacionService.consultarPostulaciones()).thenReturn(postulaciones);

        mockMvc.perform(get("/postulaciones/consultarPostulaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Postulaciones encontradas"))
                .andExpect(jsonPath("$.data[0].idPostulacion").value(1));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void consultarPostulaciones_ServiceException() throws Exception {
        when(postulacionService.consultarPostulaciones())
                .thenThrow(new RuntimeException("Error al consultar"));

        mockMvc.perform(get("/postulaciones/consultarPostulaciones"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value(-1))
                .andExpect(jsonPath("$.mensaje").value("Error al consultar"));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void misPostulaciones_Success() throws Exception {
        List<PostulacionResponseDTO> postulaciones = Arrays.asList(postulacionResponse);
        when(postulacionService.consultarMisPostulaciones()).thenReturn(postulaciones);

        mockMvc.perform(get("/postulaciones/misPostulaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Postulaciones encontradas"))
                .andExpect(jsonPath("$.data[0].idPostulacion").value(1));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void obtenerPostulacionPorPasantia_Success() throws Exception {
        when(postulacionService.obtenerPostulacionPorPasantia(1))
                .thenReturn(Optional.of(postulacionResponse));

        mockMvc.perform(get("/postulaciones/porPasantia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Postulación encontrada"))
                .andExpect(jsonPath("$.data.idPostulacion").value(1));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void obtenerPostulacionPorPasantia_NotFound() throws Exception {
        when(postulacionService.obtenerPostulacionPorPasantia(999))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/postulaciones/porPasantia/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("No se encontró postulación para esta pasantía"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(roles = {"EMPRESA"})
    void obtenerTodasPostulacionesPorPasantia_Success() throws Exception {
        List<PostulacionResponseDTO> postulaciones = Arrays.asList(postulacionResponse);
        when(postulacionService.obtenerTodasPostulacionesPorPasantia(1))
                .thenReturn(postulaciones);

        mockMvc.perform(get("/postulaciones/pasantia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Postulaciones encontradas: 1"))
                .andExpect(jsonPath("$.data[0].idPostulacion").value(1));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void obtenerTodasPostulacionesPorPasantia_ServiceException() throws Exception {
        when(postulacionService.obtenerTodasPostulacionesPorPasantia(1))
                .thenThrow(new RuntimeException("Error al obtener"));

        mockMvc.perform(get("/postulaciones/pasantia/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value(-1))
                .andExpect(jsonPath("$.mensaje").value("Error al obtener postulaciones: Error al obtener"));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void obtenerPostulacionPorId_Success() throws Exception {
        when(postulacionService.obtenerPostulacionPorId(1))
                .thenReturn(postulacionDetalle);

        mockMvc.perform(get("/postulaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.mensaje").value("Postulación encontrada"))
                .andExpect(jsonPath("$.data.idPostulacion").value(1));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void obtenerPostulacionPorId_NotFound() throws Exception {
        when(postulacionService.obtenerPostulacionPorId(999))
                .thenThrow(new IllegalArgumentException("Postulación no encontrada"));

        mockMvc.perform(get("/postulaciones/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(-1))
                .andExpect(jsonPath("$.mensaje").value("Postulación no encontrada"));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void obtenerPostulacionPorId_ServiceException() throws Exception {
        when(postulacionService.obtenerPostulacionPorId(1))
                .thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/postulaciones/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value(-1))
                .andExpect(jsonPath("$.mensaje").value("Error al obtener la postulación: Error inesperado"));
    }
}

