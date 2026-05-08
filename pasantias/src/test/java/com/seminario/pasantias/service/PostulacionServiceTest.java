package com.seminario.pasantias.service;

import com.seminario.pasantias.dto.request.PostulacionRequestDTO;
import com.seminario.pasantias.dto.request.ActualizarEstadoPostulacionDTO;
import com.seminario.pasantias.dto.response.PostulacionResponseDTO;
import com.seminario.pasantias.entity.Carrera;
import com.seminario.pasantias.entity.Empresa;
import com.seminario.pasantias.entity.EstadoPasantia;
import com.seminario.pasantias.entity.EstadoPostulacion;
import com.seminario.pasantias.entity.Estudiante;
import com.seminario.pasantias.entity.Pasantia;
import com.seminario.pasantias.entity.Postulacion;
import com.seminario.pasantias.persistence.EmpresaMapper;
import com.seminario.pasantias.persistence.EstudianteMapper;
import com.seminario.pasantias.persistence.PasantiaMapper;
import com.seminario.pasantias.persistence.PostulacionMapper;
import com.seminario.pasantias.util.PostulacionMapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostulacionServiceTest {

    private static final int ID_ESTUDIANTE = 7;
    private static final int ID_PASANTIA = 10;

    @Mock
    private PostulacionMapper postulacionMapper;
    @Mock
    private EstudianteMapper estudianteMapper;
    @Mock
    private PasantiaMapper pasantiaMapper;
    @Mock
    private EmpresaMapper empresaMapper;
    @Mock
    private PostulacionMapperUtil mapperUtil;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private PostulacionService postulacionService;

    @BeforeEach
    void wireInsertSetsGeneratedKey() {
        lenient().doAnswer(invocation -> {
            Postulacion p = invocation.getArgument(0);
            p.setIdPostulacion(100);
            return null;
        }).when(postulacionMapper).insert(any(Postulacion.class));
    }

    @Test
    void crearPostulacion_shouldFailWhenEstudianteNotFound() {
        PostulacionRequestDTO request = baseRequest();
        when(estudianteMapper.findById(ID_ESTUDIANTE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postulacionService.crearPostulacion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El estudiante con ID " + ID_ESTUDIANTE);

        verify(pasantiaMapper, never()).findById(anyInt());
    }

    @Test
    void crearPostulacion_shouldFailWhenPasantiaNotFound() {
        PostulacionRequestDTO request = baseRequest();
        when(estudianteMapper.findById(ID_ESTUDIANTE)).thenReturn(Optional.of(estudianteWithCareer("Ingeniería Industrial")));
        when(pasantiaMapper.findById(ID_PASANTIA)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postulacionService.crearPostulacion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La pasantía con ID " + ID_PASANTIA);
    }

    @Test
    void crearPostulacion_shouldFailWhenCareerNotAllowed() {
        PostulacionRequestDTO request = baseRequest();
        Estudiante estudiante = estudianteWithCareer("Ingeniería Industrial");
        Pasantia pasantia = publishedPasantia(LocalDate.now().plusDays(30));

        when(estudianteMapper.findById(ID_ESTUDIANTE)).thenReturn(Optional.of(estudiante));
        when(pasantiaMapper.findById(ID_PASANTIA)).thenReturn(Optional.of(pasantia));
        when(pasantiaMapper.findCarrerasByPasantiaId(ID_PASANTIA))
                .thenReturn(List.of(carrera("Ingeniería en Sistemas")));

        assertThatThrownBy(() -> postulacionService.crearPostulacion(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Carrera no permitida");
    }

    @Test
    void crearPostulacion_shouldSkipCareerCheckWhenEspecialidadBlank() {
        PostulacionRequestDTO request = baseRequest();
        Estudiante estudiante = new Estudiante();
        estudiante.setIdEstudiante(ID_ESTUDIANTE);
        estudiante.setEspecialidad("   ");

        Pasantia pasantia = publishedPasantia(LocalDate.now().plusDays(30));
        Empresa empresa = new Empresa();
        empresa.setIdUsuario(4);
        pasantia.setEmpresa(empresa);

        when(estudianteMapper.findById(ID_ESTUDIANTE)).thenReturn(Optional.of(estudiante));
        when(pasantiaMapper.findById(ID_PASANTIA)).thenReturn(Optional.of(pasantia));
        when(postulacionMapper.existsByEstudianteAndPasantia(ID_ESTUDIANTE, ID_PASANTIA)).thenReturn(false);

        Postulacion mapped = new Postulacion();
        when(mapperUtil.requestDtoToEntity(request)).thenReturn(mapped);
        when(postulacionMapper.findByIdWithRelations(100)).thenReturn(Optional.of(mapped));
        when(mapperUtil.entityToResponseDto(any())).thenAnswer(inv -> {
            PostulacionResponseDTO dto = new PostulacionResponseDTO();
            dto.setIdPostulacion(100);
            return dto;
        });

        PostulacionResponseDTO response = postulacionService.crearPostulacion(request);

        assertThat(response.getIdPostulacion()).isEqualTo(100);
        verify(pasantiaMapper, never()).findCarrerasByPasantiaId(anyInt());
        verify(notificacionService).crearNotificacion(eq(4), contains("Nueva postulación"));
    }

    @Test
    void crearPostulacion_shouldFailWhenPasantiaNotPublished() {
        PostulacionRequestDTO request = baseRequest();
        Estudiante estudiante = estudianteWithCareer("Ingeniería Industrial");
        Pasantia pasantia = publishedPasantia(LocalDate.now().plusDays(30));
        pasantia.setEstado(EstadoPasantia.PENDIENTE_DE_APROBACION);

        when(estudianteMapper.findById(ID_ESTUDIANTE)).thenReturn(Optional.of(estudiante));
        when(pasantiaMapper.findById(ID_PASANTIA)).thenReturn(Optional.of(pasantia));
        when(pasantiaMapper.findCarrerasByPasantiaId(ID_PASANTIA))
                .thenReturn(List.of(carrera("Ingeniería Industrial")));

        assertThatThrownBy(() -> postulacionService.crearPostulacion(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no está disponible para postulaciones");
    }

    @Test
    void crearPostulacion_shouldFailWhenPasantiaExpired() {
        PostulacionRequestDTO request = baseRequest();
        Estudiante estudiante = estudianteWithCareer("Ingeniería Industrial");
        Pasantia pasantia = publishedPasantia(LocalDate.now().minusDays(1));

        when(estudianteMapper.findById(ID_ESTUDIANTE)).thenReturn(Optional.of(estudiante));
        when(pasantiaMapper.findById(ID_PASANTIA)).thenReturn(Optional.of(pasantia));
        when(pasantiaMapper.findCarrerasByPasantiaId(ID_PASANTIA))
                .thenReturn(List.of(carrera("Ingeniería Industrial")));

        assertThatThrownBy(() -> postulacionService.crearPostulacion(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("caducado");
    }

    @Test
    void crearPostulacion_shouldFailWhenDuplicateApplication() {
        PostulacionRequestDTO request = baseRequest();
        Estudiante estudiante = estudianteWithCareer("Ingeniería Industrial");
        Pasantia pasantia = publishedPasantia(LocalDate.now().plusDays(30));

        when(estudianteMapper.findById(ID_ESTUDIANTE)).thenReturn(Optional.of(estudiante));
        when(pasantiaMapper.findById(ID_PASANTIA)).thenReturn(Optional.of(pasantia));
        when(pasantiaMapper.findCarrerasByPasantiaId(ID_PASANTIA))
                .thenReturn(List.of(carrera("Ingeniería Industrial")));
        when(postulacionMapper.existsByEstudianteAndPasantia(ID_ESTUDIANTE, ID_PASANTIA)).thenReturn(true);

        assertThatThrownBy(() -> postulacionService.crearPostulacion(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya tiene una postulación para esta pasantía");

        verify(postulacionMapper, never()).insert(any());
    }

    @Test
    void crearPostulacion_shouldNotifyEmpresaWhenUsuarioPresent() {
        PostulacionRequestDTO request = baseRequest();
        Estudiante estudiante = estudianteWithCareer("Ingeniería Industrial");
        Pasantia pasantia = publishedPasantia(LocalDate.now().plusDays(30));
        Empresa empresa = new Empresa();
        empresa.setIdUsuario(4);
        pasantia.setEmpresa(empresa);

        when(estudianteMapper.findById(ID_ESTUDIANTE)).thenReturn(Optional.of(estudiante));
        when(pasantiaMapper.findById(ID_PASANTIA)).thenReturn(Optional.of(pasantia));
        when(pasantiaMapper.findCarrerasByPasantiaId(ID_PASANTIA))
                .thenReturn(List.of(carrera("Ingeniería Industrial")));
        when(postulacionMapper.existsByEstudianteAndPasantia(ID_ESTUDIANTE, ID_PASANTIA)).thenReturn(false);

        Postulacion mapped = new Postulacion();
        when(mapperUtil.requestDtoToEntity(request)).thenReturn(mapped);
        when(postulacionMapper.findByIdWithRelations(100)).thenReturn(Optional.of(mapped));
        when(mapperUtil.entityToResponseDto(any())).thenAnswer(inv -> {
            PostulacionResponseDTO dto = new PostulacionResponseDTO();
            dto.setIdPostulacion(100);
            return dto;
        });

        PostulacionResponseDTO response = postulacionService.crearPostulacion(request);

        assertThat(response.getIdPostulacion()).isEqualTo(100);
        verify(notificacionService).crearNotificacion(eq(4), contains("Pasantía QA"));
    }

    @Test
    void crearPostulacion_shouldNotNotifyWhenEmpresaHasNoUsuario() {
        PostulacionRequestDTO request = baseRequest();
        Estudiante estudiante = estudianteWithCareer("Ingeniería Industrial");
        Pasantia pasantia = publishedPasantia(LocalDate.now().plusDays(30));
        Empresa empresa = new Empresa();
        empresa.setIdUsuario(null);
        pasantia.setEmpresa(empresa);

        when(estudianteMapper.findById(ID_ESTUDIANTE)).thenReturn(Optional.of(estudiante));
        when(pasantiaMapper.findById(ID_PASANTIA)).thenReturn(Optional.of(pasantia));
        when(pasantiaMapper.findCarrerasByPasantiaId(ID_PASANTIA))
                .thenReturn(List.of(carrera("Ingeniería Industrial")));
        when(postulacionMapper.existsByEstudianteAndPasantia(ID_ESTUDIANTE, ID_PASANTIA)).thenReturn(false);

        Postulacion mapped = new Postulacion();
        when(mapperUtil.requestDtoToEntity(request)).thenReturn(mapped);
        when(postulacionMapper.findByIdWithRelations(100)).thenReturn(Optional.of(mapped));
        when(mapperUtil.entityToResponseDto(any())).thenAnswer(inv -> {
            PostulacionResponseDTO dto = new PostulacionResponseDTO();
            dto.setIdPostulacion(100);
            return dto;
        });

        postulacionService.crearPostulacion(request);

        verify(notificacionService, never()).crearNotificacion(any(), any());
    }

    @Test
    void actualizarPostulacion_shouldFailWhenNotFound() {
        PostulacionRequestDTO request = new PostulacionRequestDTO();
        when(postulacionMapper.findById(123)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postulacionService.actualizarPostulacion(123, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Postulación no encontrada");
    }

    @Test
    void actualizarPostulacion_shouldFailWhenEstadoNotEditable() {
        Postulacion existing = new Postulacion();
        existing.setIdPostulacion(123);
        existing.setEstado(EstadoPostulacion.PUBLICADA);
        when(postulacionMapper.findById(123)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> postulacionService.actualizarPostulacion(123, new PostulacionRequestDTO()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Solo se pueden modificar");

        verify(postulacionMapper, never()).update(any());
    }

    @Test
    void actualizarPostulacion_shouldUpdateWhenEstadoEditable() {
        Postulacion existing = new Postulacion();
        existing.setIdPostulacion(123);
        existing.setEstado(EstadoPostulacion.BORRADOR);
        when(postulacionMapper.findById(123)).thenReturn(Optional.of(existing));

        PostulacionRequestDTO req = new PostulacionRequestDTO();
        Postulacion withRelations = new Postulacion();
        when(postulacionMapper.findByIdWithRelations(123)).thenReturn(Optional.of(withRelations));
        when(mapperUtil.entityToResponseDto(withRelations)).thenReturn(new PostulacionResponseDTO());

        PostulacionResponseDTO out = postulacionService.actualizarPostulacion(123, req);
        assertThat(out).isNotNull();
        verify(mapperUtil).updateEntityFromRequestDto(req, existing);
        verify(postulacionMapper).update(existing);
    }

    @Test
    void actualizarEstado_shouldFailWhenNotFound() {
        when(postulacionMapper.findByIdWithRelations(123)).thenReturn(Optional.empty());

        ActualizarEstadoPostulacionDTO req = new ActualizarEstadoPostulacionDTO();
        req.setEstado(EstadoPostulacion.PUBLICADA);

        assertThatThrownBy(() -> postulacionService.actualizarEstado(123, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Postulación no encontrada");
    }

    @Test
    void actualizarEstado_shouldEnforceTransitionRules() {
        Postulacion p = new Postulacion();
        p.setIdPostulacion(123);
        p.setEstado(EstadoPostulacion.BORRADOR);
        when(postulacionMapper.findByIdWithRelations(123)).thenReturn(Optional.of(p));

        ActualizarEstadoPostulacionDTO req = new ActualizarEstadoPostulacionDTO();
        req.setEstado(EstadoPostulacion.PUBLICADA); // BORRADOR -> PUBLICADA no permitido

        assertThatThrownBy(() -> postulacionService.actualizarEstado(123, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Desde BORRADOR");

        verify(postulacionMapper, never()).update(any());
    }

    @Test
    void actualizarEstado_shouldRequireContractFieldsWhenCubierta() {
        Postulacion p = new Postulacion();
        p.setIdPostulacion(123);
        p.setEstado(EstadoPostulacion.PUBLICADA);
        when(postulacionMapper.findByIdWithRelations(123)).thenReturn(Optional.of(p));

        ActualizarEstadoPostulacionDTO req = new ActualizarEstadoPostulacionDTO();
        req.setEstado(EstadoPostulacion.CUBIERTA);
        // missing fechaInicioContrato/duracionMeses

        assertThatThrownBy(() -> postulacionService.actualizarEstado(123, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Para estado CUBIERTA");
    }

    @Test
    void actualizarEstado_shouldUpdateWhenTransitionValid() {
        Postulacion p = new Postulacion();
        p.setIdPostulacion(123);
        p.setEstado(EstadoPostulacion.BORRADOR);
        when(postulacionMapper.findByIdWithRelations(123)).thenReturn(Optional.of(p));

        ActualizarEstadoPostulacionDTO req = new ActualizarEstadoPostulacionDTO();
        req.setEstado(EstadoPostulacion.PENDIENTE_APROBACION);

        PostulacionResponseDTO mapped = new PostulacionResponseDTO();
        when(mapperUtil.entityToResponseDto(p)).thenReturn(mapped);

        PostulacionResponseDTO out = postulacionService.actualizarEstado(123, req);
        assertThat(out).isSameAs(mapped);
        verify(postulacionMapper).update(p);
    }

    @Test
    void eliminarPostulacion_shouldFailWhenNotFound() {
        when(postulacionMapper.findById(123)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postulacionService.eliminarPostulacion(123))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Postulación no encontrada");
    }

    @Test
    void eliminarPostulacion_shouldFailWhenNotBorrador() {
        Postulacion p = new Postulacion();
        p.setIdPostulacion(123);
        p.setEstado(EstadoPostulacion.PUBLICADA);
        when(postulacionMapper.findById(123)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> postulacionService.eliminarPostulacion(123))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Solo se pueden eliminar");

        verify(postulacionMapper, never()).delete(anyInt());
    }

    @Test
    void eliminarPostulacion_shouldDeleteWhenBorrador() {
        Postulacion p = new Postulacion();
        p.setIdPostulacion(123);
        p.setEstado(EstadoPostulacion.BORRADOR);
        when(postulacionMapper.findById(123)).thenReturn(Optional.of(p));

        postulacionService.eliminarPostulacion(123);
        verify(postulacionMapper).delete(123);
    }

    private PostulacionRequestDTO baseRequest() {
        PostulacionRequestDTO request = new PostulacionRequestDTO();
        request.setIdEstudiante(ID_ESTUDIANTE);
        request.setIdPasantia(ID_PASANTIA);
        request.setFechaPostulacion(LocalDate.now());
        request.setEstado(EstadoPostulacion.PENDIENTE_APROBACION);
        return request;
    }

    private Estudiante estudianteWithCareer(String especialidad) {
        Estudiante e = new Estudiante();
        e.setIdEstudiante(ID_ESTUDIANTE);
        e.setEspecialidad(especialidad);
        return e;
    }

    private Pasantia publishedPasantia(LocalDate fechaCaducidad) {
        Pasantia p = new Pasantia();
        p.setIdPasantia(ID_PASANTIA);
        p.setEstado(EstadoPasantia.PUBLICADA);
        p.setFechaCaducidad(fechaCaducidad);
        p.setTitulo("Pasantía QA");
        return p;
    }

    private Carrera carrera(String nombre) {
        Carrera c = new Carrera();
        c.setNombre(nombre);
        return c;
    }
}
