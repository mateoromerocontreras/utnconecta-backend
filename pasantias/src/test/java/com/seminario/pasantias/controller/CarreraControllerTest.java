package com.seminario.pasantias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.CarreraRequest;
import com.seminario.pasantias.entity.Carrera;
import com.seminario.pasantias.service.CarreraService;
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
class CarreraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarreraService carreraService;

    private Carrera carrera;
    private CarreraRequest carreraRequest;

    @BeforeEach
    void setUp() {
        carrera = new Carrera();
        carrera.setId(1);
        carrera.setNombre("Ingeniería en Sistemas");

        carreraRequest = new CarreraRequest();
        carreraRequest.setId(1);
        carreraRequest.setNombre("Ingeniería en Sistemas");
    }

    @Test
    void registrarCarrera_Success() throws Exception {
        doNothing().when(carreraService).createCarrera(any(Carrera.class));

        mockMvc.perform(post("/carreras/registrarCarrera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carreraRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void registrarCarrera_MissingNombre() throws Exception {
        carreraRequest.setNombre(null);

        mockMvc.perform(post("/carreras/registrarCarrera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carreraRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
    }

    @Test
    void registrarCarrera_EmptyNombre() throws Exception {
        carreraRequest.setNombre("");

        mockMvc.perform(post("/carreras/registrarCarrera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carreraRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
    }

    @Test
    void registrarCarrera_ServiceException() throws Exception {
        doThrow(new RuntimeException("Error al crear carrera")).when(carreraService)
                .createCarrera(any(Carrera.class));

        mockMvc.perform(post("/carreras/registrarCarrera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carreraRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Error al crear carrera"));
    }

    @Test
    void consultarCarrera_NoParams() throws Exception {
        List<Carrera> carreras = Arrays.asList(carrera);
        when(carreraService.getAllCarreras()).thenReturn(carreras);

        mockMvc.perform(get("/carreras/consultarCarrera"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Ingeniería en Sistemas"));
    }

    @Test
    void consultarCarrera_ByNombre() throws Exception {
        when(carreraService.getCarreraByNombre("Ingeniería en Sistemas"))
                .thenReturn(Optional.of(carrera));

        mockMvc.perform(get("/carreras/consultarCarrera")
                        .param("nombre", "Ingeniería en Sistemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Ingeniería en Sistemas"));
    }

    @Test
    void consultarCarrera_ByNombreNotFound() throws Exception {
        when(carreraService.getCarreraByNombre("No existe")).thenReturn(Optional.empty());

        mockMvc.perform(get("/carreras/consultarCarrera")
                        .param("nombre", "No existe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateCarrera_Success() throws Exception {
        doNothing().when(carreraService).updateCarrera(any(Carrera.class));

        mockMvc.perform(post("/carreras/updateCarrera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carreraRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void updateCarrera_MissingId() throws Exception {
        carreraRequest.setId(null);

        mockMvc.perform(post("/carreras/updateCarrera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carreraRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El id y el nombre son obligatorios"));
    }

    @Test
    void updateCarrera_MissingNombre() throws Exception {
        carreraRequest.setNombre(null);

        mockMvc.perform(post("/carreras/updateCarrera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carreraRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El id y el nombre son obligatorios"));
    }

    @Test
    void deleteCarrera_Success() throws Exception {
        CarreraRequest deleteRequest = new CarreraRequest();
        deleteRequest.setNombre("Ingeniería en Sistemas");
        doNothing().when(carreraService).deleteCarreraByNombre("Ingeniería en Sistemas");

        mockMvc.perform(post("/carreras/deleteCarrera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void deleteCarrera_MissingNombre() throws Exception {
        CarreraRequest deleteRequest = new CarreraRequest();
        deleteRequest.setNombre(null);

        mockMvc.perform(post("/carreras/deleteCarrera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
    }

    @Test
    void listarCarreras_Success() throws Exception {
        List<Carrera> carreras = Arrays.asList(carrera);
        when(carreraService.getAllCarreras()).thenReturn(carreras);

        mockMvc.perform(get("/carreras/listarCarreras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Ingeniería en Sistemas"))
                .andExpect(jsonPath("$[0].id").value(1));
    }
}

