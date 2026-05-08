package com.seminario.pasantias.service;

import com.seminario.pasantias.dto.EstudianteBasicResponse;
import com.seminario.pasantias.dto.EstudianteUpdateProfileRequest;
import com.seminario.pasantias.dto.EstudianteUpdateRequest;
import com.seminario.pasantias.entity.Estudiante;
import com.seminario.pasantias.entity.Rol;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.persistence.EstudianteMapper;
import com.seminario.pasantias.persistence.UsuarioMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteMapper estudianteMapper;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EstudianteService estudianteService;

    @Test
    void createEstudiante_shouldFailWhenUsuarioMissing() {
        when(usuarioMapper.findById(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estudianteService.createEstudiante("a@b.com", 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");

        verify(estudianteMapper, never()).insert(any());
    }

    @Test
    void createEstudiante_shouldFailWhenEstudianteAlreadyExistsForUsuario() {
        when(usuarioMapper.findById(10)).thenReturn(Optional.of(new Usuario()));
        when(estudianteMapper.findByUsuarioId(10)).thenReturn(Optional.of(new Estudiante()));

        assertThatThrownBy(() -> estudianteService.createEstudiante("a@b.com", 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un perfil de estudiante");

        verify(estudianteMapper, never()).insert(any());
    }

    @Test
    void createEstudiante_shouldInsertAndReturn() {
        when(usuarioMapper.findById(10)).thenReturn(Optional.of(new Usuario()));
        when(estudianteMapper.findByUsuarioId(10)).thenReturn(Optional.empty());

        Estudiante created = estudianteService.createEstudiante("a@b.com", 10);

        assertThat(created.getEmail()).isEqualTo("a@b.com");
        assertThat(created.getIdUsuario()).isEqualTo(10);
        assertThat(created.getActivo()).isTrue();
        assertThat(created.getFechaCreacion()).isNotNull();
        verify(estudianteMapper).insert(created);
    }

    @Test
    void createEstudianteBasico_shouldInsertAndReturn() {
        when(usuarioMapper.findById(10)).thenReturn(Optional.of(new Usuario()));
        when(estudianteMapper.findByUsuarioId(10)).thenReturn(Optional.empty());

        Estudiante created = estudianteService.createEstudianteBasico(
                "Juan", "Perez", "123", "351", "a@b.com", 10
        );

        assertThat(created.getNombre()).isEqualTo("Juan");
        assertThat(created.getApellido()).isEqualTo("Perez");
        assertThat(created.getDni()).isEqualTo("123");
        assertThat(created.getTelCelular()).isEqualTo("351");
        assertThat(created.getEmail()).isEqualTo("a@b.com");
        assertThat(created.getActivo()).isTrue();
        verify(estudianteMapper).insert(created);
    }

    @Test
    void updateEstudiante_shouldFailWhenProfileMissing() {
        when(estudianteMapper.findByUsuarioId(10)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estudianteService.updateEstudiante(10, new EstudianteUpdateRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Perfil de estudiante no encontrado");

        verify(estudianteMapper, never()).update(any());
    }

    @Test
    void updateEstudiante_shouldUpdateOnlyProvidedFields() {
        Estudiante e = new Estudiante();
        e.setDni("oldDni");
        e.setApellido("oldApellido");
        when(estudianteMapper.findByUsuarioId(10)).thenReturn(Optional.of(e));

        EstudianteUpdateRequest req = new EstudianteUpdateRequest();
        req.setDni("newDni");
        req.setApellido(""); // should be ignored
        req.setNroCalle(123); // should set

        estudianteService.updateEstudiante(10, req);

        assertThat(e.getDni()).isEqualTo("newDni");
        assertThat(e.getApellido()).isEqualTo("oldApellido");
        assertThat(e.getNroCalle()).isEqualTo(123);
        verify(estudianteMapper).update(e);
    }

    @Test
    void getOrCreateEstudianteProfile_shouldReturnExisting() {
        Estudiante e = new Estudiante();
        when(estudianteMapper.findByUsuarioId(10)).thenReturn(Optional.of(e));

        Estudiante out = estudianteService.getOrCreateEstudianteProfile("a@b.com", 10);
        assertThat(out).isSameAs(e);
        verify(estudianteMapper, never()).insert(any());
    }

    @Test
    void getOrCreateEstudianteProfile_shouldCreateWhenMissing() {
        when(estudianteMapper.findByUsuarioId(10)).thenReturn(Optional.empty());
        when(usuarioMapper.findById(10)).thenReturn(Optional.of(new Usuario()));

        Estudiante out = estudianteService.getOrCreateEstudianteProfile("a@b.com", 10);
        assertThat(out.getEmail()).isEqualTo("a@b.com");
        verify(estudianteMapper).insert(out);
    }

    @Test
    void getAllEstudiantesBasic_shouldMapToBasicResponse() {
        Estudiante e = new Estudiante();
        e.setIdEstudiante(1);
        e.setNombre("Juan");
        e.setEmail("a@b.com");
        when(estudianteMapper.findAllActive()).thenReturn(List.of(e));

        List<EstudianteBasicResponse> out = estudianteService.getAllEstudiantesBasic();
        assertThat(out).hasSize(1);
        assertThat(out.getFirst().getIdEstudiante()).isEqualTo(1);
        assertThat(out.getFirst().getNombre()).isEqualTo("Juan");
        assertThat(out.getFirst().getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void getEstudianteByNombreBasic_shouldMapWhenPresent() {
        Estudiante e = new Estudiante();
        e.setIdEstudiante(2);
        e.setNombre("Ana");
        when(estudianteMapper.findByNombre("Ana")).thenReturn(Optional.of(e));

        Optional<EstudianteBasicResponse> out = estudianteService.getEstudianteByNombreBasic("Ana");
        assertThat(out).isPresent();
        assertThat(out.orElseThrow().getIdEstudiante()).isEqualTo(2);
    }

    @Test
    void updateEstudianteProfile_shouldFailWhenUsuarioMissing() {
        when(usuarioMapper.findByEmail("old@b.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estudianteService.updateEstudianteProfile("old@b.com", new EstudianteUpdateProfileRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    void updateEstudianteProfile_shouldFailWhenRolNotEstudiante() {
        Usuario u = new Usuario();
        Rol rol = new Rol();
        rol.setNombre("EMPRESA");
        u.setRol(rol);
        when(usuarioMapper.findByEmail("old@b.com")).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> estudianteService.updateEstudianteProfile("old@b.com", new EstudianteUpdateProfileRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no es un estudiante");
    }

    @Test
    void updateEstudianteProfile_shouldFailWhenNewEmailAlreadyExists() {
        Usuario u = usuarioEstudiante("old@b.com");
        u.setIdUsuario(10);
        when(usuarioMapper.findByEmail("old@b.com")).thenReturn(Optional.of(u));
        when(usuarioMapper.findByEmail("new@b.com")).thenReturn(Optional.of(new Usuario()));

        EstudianteUpdateProfileRequest req = new EstudianteUpdateProfileRequest();
        req.setEmail("new@b.com");

        assertThatThrownBy(() -> estudianteService.updateEstudianteProfile("old@b.com", req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con ese email");

        verify(usuarioMapper, never()).update(any());
    }

    @Test
    void updateEstudianteProfile_shouldUpdateEmailAndStudentEmailWhenDifferent() {
        Usuario u = usuarioEstudiante("old@b.com");
        u.setIdUsuario(10);
        when(usuarioMapper.findByEmail("old@b.com")).thenReturn(Optional.of(u));
        when(usuarioMapper.findByEmail("new@b.com")).thenReturn(Optional.empty());

        Estudiante e = new Estudiante();
        e.setIdUsuario(10);
        e.setEmail("old@b.com");
        when(estudianteMapper.findByUsuarioId(10)).thenReturn(Optional.of(e));

        EstudianteUpdateProfileRequest req = new EstudianteUpdateProfileRequest();
        req.setEmail(" new@b.com ");

        estudianteService.updateEstudianteProfile("old@b.com", req);

        assertThat(u.getEmail()).isEqualTo("new@b.com");
        assertThat(u.getUsername()).isEqualTo("new@b.com");
        assertThat(e.getEmail()).isEqualTo("new@b.com");
        verify(estudianteMapper).update(e);
        verify(usuarioMapper).update(u);
    }

    @Test
    void updateEstudianteProfile_shouldNotUpdateWhenEmailSameAndPasswordBlank() {
        Usuario u = usuarioEstudiante("old@b.com");
        when(usuarioMapper.findByEmail("old@b.com")).thenReturn(Optional.of(u));

        EstudianteUpdateProfileRequest req = new EstudianteUpdateProfileRequest();
        req.setEmail(" old@b.com ");
        req.setPassword("   ");

        estudianteService.updateEstudianteProfile("old@b.com", req);

        verify(usuarioMapper, never()).update(any());
        verify(estudianteMapper, never()).update(any());
    }

    @Test
    void updateEstudianteProfile_shouldFailWhenPasswordWeak() {
        Usuario u = usuarioEstudiante("old@b.com");
        when(usuarioMapper.findByEmail("old@b.com")).thenReturn(Optional.of(u));

        EstudianteUpdateProfileRequest req = new EstudianteUpdateProfileRequest();
        req.setPassword("abcdefg"); // < 8, no number

        assertThatThrownBy(() -> estudianteService.updateEstudianteProfile("old@b.com", req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("La contraseña debe tener");

        verify(usuarioMapper, never()).update(any());
    }

    @Test
    void updateEstudianteProfile_shouldEncodePasswordAndUpdate() {
        Usuario u = usuarioEstudiante("old@b.com");
        when(usuarioMapper.findByEmail("old@b.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("abcd1234")).thenReturn("hashed");

        EstudianteUpdateProfileRequest req = new EstudianteUpdateProfileRequest();
        req.setPassword(" abcd1234 ");

        estudianteService.updateEstudianteProfile("old@b.com", req);

        assertThat(u.getPassword()).isEqualTo("hashed");
        verify(usuarioMapper).update(u);
    }

    @Test
    void deactivateEstudiante_shouldCallMapper() {
        estudianteService.deactivateEstudiante(1);
        verify(estudianteMapper).deactivate(1);
    }

    @Test
    void deleteEstudiante_shouldCallMapper() {
        estudianteService.deleteEstudiante(1);
        verify(estudianteMapper).delete(1);
    }

    private Usuario usuarioEstudiante(String email) {
        Usuario u = new Usuario();
        Rol rol = new Rol();
        rol.setNombre("ESTUDIANTE");
        u.setRol(rol);
        u.setEmail(email);
        u.setUsername(email);
        return u;
    }
}

