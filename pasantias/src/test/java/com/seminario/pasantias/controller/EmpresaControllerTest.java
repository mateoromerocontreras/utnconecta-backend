package com.seminario.pasantias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.EmpresaRequest;
import com.seminario.pasantias.entity.Contacto;
import com.seminario.pasantias.entity.Empresa;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.EmpresaService;
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
class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmpresaService empresaService;

    private Empresa empresa;
    private EmpresaRequest empresaRequest;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setIdEmpresa(1);
        empresa.setNombre("Test Empresa");
        empresa.setCuit("20-12345678-9");
        empresa.setRazonSocial("Test S.A.");
        empresa.setCiudad("Buenos Aires");
        empresa.setCalle("Av. Test");
        empresa.setNroCalle(123);

        empresaRequest = new EmpresaRequest();
        empresaRequest.setNombre("Test Empresa");
        empresaRequest.setCuit("20-12345678-9");
        empresaRequest.setRazonSocial("Test S.A.");
        empresaRequest.setCiudad("Buenos Aires");
        empresaRequest.setCalle("Av. Test");
        empresaRequest.setNroCalle(123);
        
        Contacto contacto = new Contacto();
        contacto.setNombre("Juan");
        contacto.setApellido("Perez");
        contacto.setEmailResponsable("juan@example.com");
        empresaRequest.setContacto(Arrays.asList(contacto));
    }

    @Test
    void getAllEmpresas_Success() throws Exception {
        List<Empresa> empresas = Arrays.asList(empresa);
        when(empresaService.getAllEmpresas()).thenReturn(empresas);

        mockMvc.perform(get("/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Test Empresa"))
                .andExpect(jsonPath("$[0].cuit").value("20-12345678-9"));
    }

    @Test
    void getEmpresaById_Success() throws Exception {
        when(empresaService.getEmpresaById(1)).thenReturn(empresa);

        mockMvc.perform(get("/empresas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test Empresa"))
                .andExpect(jsonPath("$.cuit").value("20-12345678-9"));
    }

    @Test
    void consultarEmpresas_NoParams() throws Exception {
        List<Empresa> empresas = Arrays.asList(empresa);
        when(empresaService.getAllEmpresas()).thenReturn(empresas);

        mockMvc.perform(get("/empresas/consultarEmpresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Test Empresa"));
    }

    @Test
    void consultarEmpresas_ByCuit() throws Exception {
        when(empresaService.getEmpresaByCuit("20-12345678-9")).thenReturn(Optional.of(empresa));

        mockMvc.perform(get("/empresas/consultarEmpresas")
                        .param("cuit", "20-12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Test Empresa"));
    }

    @Test
    void consultarEmpresas_ByCuitNotFound() throws Exception {
        when(empresaService.getEmpresaByCuit("20-99999999-9")).thenReturn(Optional.empty());

        mockMvc.perform(get("/empresas/consultarEmpresas")
                        .param("cuit", "20-99999999-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void consultarEmpresas_ByNombre() throws Exception {
        List<Empresa> empresas = Arrays.asList(empresa);
        when(empresaService.getEmpresasByNombre("Test")).thenReturn(empresas);

        mockMvc.perform(get("/empresas/consultarEmpresas")
                        .param("nombre", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Test Empresa"));
    }

    @Test
    void crearEmpresa_Success() throws Exception {
        doNothing().when(empresaService).createEmpresaWithContactos(any(Empresa.class));

        mockMvc.perform(post("/empresas/crearEmpresa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empresaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void crearEmpresa_MissingNombre() throws Exception {
        empresaRequest.setNombre(null);

        mockMvc.perform(post("/empresas/crearEmpresa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empresaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"));
    }

    @Test
    void crearEmpresa_MissingCuit() throws Exception {
        empresaRequest.setCuit(null);

        mockMvc.perform(post("/empresas/crearEmpresa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empresaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El CUIT es obligatorio"));
    }

    @Test
    void crearEmpresa_ServiceException() throws Exception {
        doThrow(new RuntimeException("Error al crear empresa")).when(empresaService)
                .createEmpresaWithContactos(any(Empresa.class));

        mockMvc.perform(post("/empresas/crearEmpresa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empresaRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Error al crear empresa"));
    }

    @Test
    void updateEmpresa_Success() throws Exception {
        doNothing().when(empresaService).updateEmpresa(any(Empresa.class));

        mockMvc.perform(put("/empresas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empresaRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEmpresaByCuit_Success() throws Exception {
        doNothing().when(empresaService).deleteEmpresaByCuit("20-12345678-9");

        mockMvc.perform(post("/empresas/deleteEmpresa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cuit\": \"20-12345678-9\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void deleteEmpresaByCuit_MissingCuit() throws Exception {
        mockMvc.perform(post("/empresas/deleteEmpresa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("El CUIT es obligatorio"));
    }

    @Test
    void deleteEmpresaById_Success() throws Exception {
        doNothing().when(empresaService).deleteEmpresa(1);

        mockMvc.perform(delete("/empresas/1"))
                .andExpect(status().isOk());
    }
}

