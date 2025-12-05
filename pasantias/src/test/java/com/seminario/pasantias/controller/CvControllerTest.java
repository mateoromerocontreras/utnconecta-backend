package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.CvDto;
import com.seminario.pasantias.entity.Cv;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.CvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
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
class CvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CvService cvService;

    private Cv cv;
    private CvDto cvDto;
    private MockMultipartFile pdfFile;
    private MockMultipartFile invalidFile;

    @BeforeEach
    void setUp() {
        cv = new Cv();
        cv.setIdCv(1);
        cv.setNombreArchivo("cv.pdf");
        cv.setDatosCv(new byte[]{1, 2, 3, 4, 5});

        cvDto = new CvDto();
        cvDto.setIdCv(1);
        cvDto.setNombreArchivo("cv.pdf");

        pdfFile = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        invalidFile = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "Text content".getBytes()
        );
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void subirCv_Success() throws Exception {
        doNothing().when(cvService).subirCv(any(), eq(1));

        mockMvc.perform(multipart("/cvs/subirCV")
                        .file(pdfFile)
                        .param("idEstudiante", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("CV subido exitosamente."));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void subirCv_AsAdministrator() throws Exception {
        doNothing().when(cvService).subirCv(any(), eq(1));

        mockMvc.perform(multipart("/cvs/subirCV")
                        .file(pdfFile)
                        .param("idEstudiante", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void subirCv_InvalidFileType() throws Exception {
        mockMvc.perform(multipart("/cvs/subirCV")
                        .file(invalidFile)
                        .param("idEstudiante", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Solo se permiten archivos PDF."));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void subirCv_ServiceException() throws Exception {
        doThrow(new IOException("Error al leer archivo")).when(cvService)
                .subirCv(any(), eq(1));

        mockMvc.perform(multipart("/cvs/subirCV")
                        .file(pdfFile)
                        .param("idEstudiante", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Error al leer el archivo: Error al leer archivo"));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void listarCvs_Success() throws Exception {
        List<CvDto> cvs = Arrays.asList(cvDto);
        when(cvService.listarCvsPorEstudiante(1)).thenReturn(cvs);

        mockMvc.perform(get("/cvs/getCV")
                        .param("idEstudiante", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCv").value(1))
                .andExpect(jsonPath("$[0].nombreArchivo").value("cv.pdf"));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void descargarCv_Success() throws Exception {
        when(cvService.descargarCv(1)).thenReturn(Optional.of(cv));

        mockMvc.perform(get("/cvs/descargarCV/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"cv.pdf\""))
                .andExpect(content().bytes(cv.getDatosCv()));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void descargarCv_NotFound() throws Exception {
        when(cvService.descargarCv(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/cvs/descargarCV/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void eliminarCv_Success() throws Exception {
        doNothing().when(cvService).eliminarCv(1);

        mockMvc.perform(delete("/cvs/eliminarCV/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("CV eliminado exitosamente."));
    }

    @Test
    @WithMockUser(roles = {"ESTUDIANTE"})
    void eliminarCv_ServiceException() throws Exception {
        doThrow(new RuntimeException("CV no encontrado")).when(cvService).eliminarCv(999);

        mockMvc.perform(delete("/cvs/eliminarCV/999")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Error al eliminar el CV: CV no encontrado"));
    }
}

