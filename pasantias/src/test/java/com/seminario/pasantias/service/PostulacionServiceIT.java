package com.seminario.pasantias.service;

import com.seminario.pasantias.dto.request.PostulacionRequestDTO;
import com.seminario.pasantias.dto.response.PostulacionResponseDTO;
import com.seminario.pasantias.entity.Empresa;
import com.seminario.pasantias.entity.EstadoPostulacion;
import com.seminario.pasantias.entity.Pasantia;
import com.seminario.pasantias.persistence.NotificacionMapper;
import com.seminario.pasantias.persistence.PasantiaMapper;
import com.seminario.pasantias.persistence.PostulacionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:sql/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
class PostulacionServiceIT {

    private Integer testPasantiaId;

    @Autowired
    private PostulacionService postulacionService;

    @Autowired
    private PostulacionMapper postulacionMapper;

    @Autowired
    private PasantiaMapper pasantiaMapper;

    @Autowired
    private NotificacionMapper notificacionMapper;

    @BeforeEach
    void setUpFreshPasantia() {
        Pasantia pasantia = new Pasantia();
        pasantia.setTitulo("Pasantía Integración Postulación");
        pasantia.setPuestoACubrir("Backend Junior");
        pasantia.setCiudad("Córdoba");
        pasantia.setModalidad("Híbrida");
        pasantia.setAsignacionEstimulo(75000f);
        pasantia.setCantidadDePasantes(1);
        pasantia.setFechaPublicacion(LocalDate.now());
        pasantia.setFechaCaducidad(LocalDate.now().plusDays(90));
        pasantia.setEstado(com.seminario.pasantias.entity.EstadoPasantia.PUBLICADA);
        pasantia.setEmailContacto("rrhh@biofarmaweb.com.ar");

        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(1);
        pasantia.setEmpresa(empresa);

        pasantiaMapper.insert(pasantia);
        // Happy path: el estudiante 2 (María López) tiene especialidad "Ingeniería Industrial" (id_carrera=2 en schema.sql)
        pasantiaMapper.insertPasantiaCarrera(pasantia.getIdPasantia(), 2);
        testPasantiaId = pasantia.getIdPasantia();
    }

    @Test
    void crearPostulacion_shouldRejectWhenCarreraNotAllowed() {
        // Student 2: "Ingeniería Industrial" (schema.sql -> id_carrera=2)
        // We create a pasantía that only allows a different career (id_carrera=6 -> "Ingeniería en Sistemas")
        Pasantia pasantia = new Pasantia();
        pasantia.setTitulo("Pasantía Integración Postulación (Carrera no permitida)");
        pasantia.setPuestoACubrir("Backend Junior");
        pasantia.setCiudad("Córdoba");
        pasantia.setModalidad("Híbrida");
        pasantia.setAsignacionEstimulo(75000f);
        pasantia.setCantidadDePasantes(1);
        pasantia.setFechaPublicacion(LocalDate.now());
        pasantia.setFechaCaducidad(LocalDate.now().plusDays(90));
        pasantia.setEstado(com.seminario.pasantias.entity.EstadoPasantia.PUBLICADA);
        pasantia.setEmailContacto("rrhh@biofarmaweb.com.ar");

        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(1);
        pasantia.setEmpresa(empresa);

        pasantiaMapper.insert(pasantia);
        pasantiaMapper.insertPasantiaCarrera(pasantia.getIdPasantia(), 6);

        PostulacionRequestDTO request = new PostulacionRequestDTO();
        request.setFechaPostulacion(LocalDate.now());
        request.setEstado(EstadoPostulacion.PENDIENTE_APROBACION);
        request.setIdPasantia(pasantia.getIdPasantia());
        request.setIdEstudiante(2);

        assertThatThrownBy(() -> postulacionService.crearPostulacion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Carrera no permitida");
    }

    @Test
    void crearPostulacion_shouldPersistRowAndReturnMappedResponse() {
        PostulacionRequestDTO request = new PostulacionRequestDTO();
        request.setFechaPostulacion(LocalDate.now());
        request.setEstado(EstadoPostulacion.PENDIENTE_APROBACION);
        request.setIdPasantia(testPasantiaId);
        request.setIdEstudiante(2);

        PostulacionResponseDTO response = postulacionService.crearPostulacion(request);

        assertThat(response.getIdPostulacion()).isNotNull();
        assertThat(response.getEstado()).isEqualTo(EstadoPostulacion.PENDIENTE_APROBACION);
        assertThat(response.getIdPasantia()).isEqualTo(testPasantiaId);
        assertThat(response.getTituloPasantia()).isEqualTo("Pasantía Integración Postulación");
        assertThat(response.getIdEstudiante()).isEqualTo(2);
        assertThat(response.getNombreEstudiante()).isEqualTo("María López");
        assertThat(response.getNombreEmpresa()).isEqualTo("BIOFARMA S.A");
        assertThat(response.getEsEditable()).isTrue();

        assertThat(postulacionMapper.existsByEstudianteAndPasantia(2, testPasantiaId)).isTrue();
        assertThat(postulacionMapper.findById(response.getIdPostulacion())).isPresent();
        assertThat(notificacionMapper.findByUsuarioId(4))
                .hasSize(1)
                .first()
                .extracting("mensaje")
                .isEqualTo("Nueva postulación recibida para la pasantía: Pasantía Integración Postulación");
    }

    @Test
    void crearPostulacion_shouldRejectDuplicateApplication() {
        PostulacionRequestDTO request = new PostulacionRequestDTO();
        request.setFechaPostulacion(LocalDate.now());
        request.setEstado(EstadoPostulacion.PENDIENTE_APROBACION);
        request.setIdPasantia(testPasantiaId);
        request.setIdEstudiante(2);

        PostulacionResponseDTO created = postulacionService.crearPostulacion(request);

        assertThat(created.getIdPostulacion()).isNotNull();

        assertThatThrownBy(() -> postulacionService.crearPostulacion(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya tiene una postulación para esta pasantía");

        assertThat(postulacionMapper.existsByEstudianteAndPasantia(2, testPasantiaId)).isTrue();
        assertThat(notificacionMapper.findByUsuarioId(4)).hasSize(1);
    }
}
