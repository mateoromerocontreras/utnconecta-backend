package com.seminario.pasantias.service;

import com.seminario.pasantias.dto.EstudianteBasicResponse;
import com.seminario.pasantias.dto.EstudianteUpdateProfileRequest;
import com.seminario.pasantias.dto.EstudianteUpdateRequest;
import com.seminario.pasantias.entity.Estudiante;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.persistence.EstudianteMapper;
import com.seminario.pasantias.persistence.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";

    public static class EstudianteServiceException extends RuntimeException {
        public EstudianteServiceException(String message) {
            super(message);
        }
    }

    @Autowired
    private EstudianteMapper estudianteMapper;
    
    @Autowired
    private UsuarioMapper usuarioMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Estudiante> findById(Integer id) {
        return estudianteMapper.findById(id);
    }

    public Optional<Estudiante> findByEmail(String email) {
        return estudianteMapper.findByEmail(email);
    }
    
    public Optional<Estudiante> findByUsuarioId(Integer idUsuario) {
        return estudianteMapper.findByUsuarioId(idUsuario);
    }

    public List<Estudiante> findAllActive() {
        return estudianteMapper.findAllActive();
    }

    public Optional<Estudiante> findByNombre(String nombre) {
        return estudianteMapper.findByNombre(nombre);
    }

    public Estudiante createEstudiante(String email, Integer idUsuario) {
        // Verificar que el usuario existe
        Optional<Usuario> usuarioOpt = usuarioMapper.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            throw new EstudianteServiceException(USUARIO_NO_ENCONTRADO);
        }
        
        // Verificar que no existe ya un estudiante para este usuario
        Optional<Estudiante> estudianteExistente = estudianteMapper.findByUsuarioId(idUsuario);
        if (estudianteExistente.isPresent()) {
            throw new EstudianteServiceException("Ya existe un perfil de estudiante para este usuario");
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setEmail(email);
        estudiante.setIdUsuario(idUsuario);
        estudiante.setActivo(true);
        estudiante.setFechaCreacion(LocalDateTime.now());

        estudianteMapper.insert(estudiante);
        return estudiante;
    }
    
    public Estudiante createEstudianteBasico(String nombre, String apellido, String dni, 
                                             String telCelular, String email, Integer idUsuario) {
        // Verificar que el usuario existe
        Optional<Usuario> usuarioOpt = usuarioMapper.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            throw new EstudianteServiceException(USUARIO_NO_ENCONTRADO);
        }
        
        // Verificar que no existe ya un estudiante para este usuario
        Optional<Estudiante> estudianteExistente = estudianteMapper.findByUsuarioId(idUsuario);
        if (estudianteExistente.isPresent()) {
            throw new EstudianteServiceException("Ya existe un perfil de estudiante para este usuario");
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(nombre);
        estudiante.setApellido(apellido);
        estudiante.setDni(dni);
        estudiante.setTelCelular(telCelular);
        estudiante.setEmail(email);
        estudiante.setIdUsuario(idUsuario);
        estudiante.setActivo(true);
        estudiante.setFechaCreacion(LocalDateTime.now());

        estudianteMapper.insert(estudiante);
        return estudiante;
    }

    public void updateEstudiante(Integer idUsuario, EstudianteUpdateRequest request) {
        // Buscar estudiante por idUsuario
        Optional<Estudiante> estudianteOpt = estudianteMapper.findByUsuarioId(idUsuario);
        if (estudianteOpt.isEmpty()) {
            throw new EstudianteServiceException("Perfil de estudiante no encontrado para el usuario");
        }
        
        Estudiante estudiante = estudianteOpt.get();
        
        applyUpdate(estudiante, request);
        
        // Actualizar en base de datos
        estudianteMapper.update(estudiante);
    }

    public void deactivateEstudiante(Integer id) {
        estudianteMapper.deactivate(id);
    }

    public void deleteEstudiante(Integer id) {
        estudianteMapper.delete(id);
    }
    
    public Estudiante getOrCreateEstudianteProfile(String email, Integer idUsuario) {
        // Buscar si ya existe un perfil para este usuario
        Optional<Estudiante> estudianteOpt = estudianteMapper.findByUsuarioId(idUsuario);
        
        if (estudianteOpt.isPresent()) {
            return estudianteOpt.get();
        } else {
            // Crear un nuevo perfil básico
            return createEstudiante(email, idUsuario);
        }
    }
    
    // Método para convertir Estudiante a EstudianteBasicResponse (sin datos sensibles)
    private EstudianteBasicResponse toBasicResponse(Estudiante estudiante) {
        EstudianteBasicResponse response = new EstudianteBasicResponse();
        response.setIdEstudiante(estudiante.getIdEstudiante());
        response.setDni(estudiante.getDni());
        response.setApellido(estudiante.getApellido());
        response.setNombre(estudiante.getNombre());
        response.setEspecialidad(estudiante.getEspecialidad());
        response.setNroLegajo(estudiante.getNroLegajo());
        response.setCalle(estudiante.getCalle());
        response.setNroCalle(estudiante.getNroCalle());
        response.setBarrio(estudiante.getBarrio());
        response.setLocalidad(estudiante.getLocalidad());
        response.setProvincia(estudiante.getProvincia());
        response.setEmail(estudiante.getEmail());
        response.setTelCelular(estudiante.getTelCelular());
        response.setTelFijo(estudiante.getTelFijo());
        response.setActivo(estudiante.getActivo());
        response.setFechaCreacion(estudiante.getFechaCreacion());
        return response;
    }
    
    // Método para obtener todos los estudiantes como respuesta básica
    public List<EstudianteBasicResponse> getAllEstudiantesBasic() {
        List<Estudiante> estudiantes = estudianteMapper.findAllActive();
        return estudiantes.stream()
                .map(this::toBasicResponse)
                .toList();
    }
    
    // Método para buscar estudiante por nombre como respuesta básica
    public Optional<EstudianteBasicResponse> getEstudianteByNombreBasic(String nombre) {
        Optional<Estudiante> estudiante = estudianteMapper.findByNombre(nombre);
        return estudiante.map(this::toBasicResponse);
    }
    
    // Método para actualizar email y/o contraseña del estudiante
    public void updateEstudianteProfile(String currentEmail, EstudianteUpdateProfileRequest request) {
        Usuario usuario = findUsuarioByEmailOrThrow(currentEmail);
        validateEsEstudiante(usuario);

        boolean needsUpdate = false;
        String newEmail = normalize(request.getEmail());
        if (shouldUpdateEmail(newEmail, currentEmail)) {
            assertEmailNoUsado(newEmail);
            updateEmail(usuario, newEmail);
            updateEmailEnEstudiante(usuario.getIdUsuario(), newEmail);
            needsUpdate = true;
        }

        String newPassword = normalize(request.getPassword());
        if (hasText(newPassword)) {
            validatePassword(newPassword);
            usuario.setPassword(passwordEncoder.encode(newPassword));
            needsUpdate = true;
        }

        if (needsUpdate) {
            usuarioMapper.update(usuario);
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isEmpty();
    }

    private static String trimmedOrNull(String value) {
        return value == null ? null : value.trim();
    }

    private static void applyUpdate(Estudiante estudiante, EstudianteUpdateRequest request) {
        setIfHasText(request.getDni(), estudiante::setDni);
        setIfHasText(request.getApellido(), estudiante::setApellido);
        setIfHasText(request.getNombre(), estudiante::setNombre);
        setIfHasText(request.getEspecialidad(), estudiante::setEspecialidad);
        setIfHasText(request.getNroLegajo(), estudiante::setNroLegajo);
        setIfHasText(request.getCalle(), estudiante::setCalle);

        if (request.getNroCalle() != null) {
            estudiante.setNroCalle(request.getNroCalle());
        }

        setIfHasText(request.getBarrio(), estudiante::setBarrio);
        setIfHasText(request.getLocalidad(), estudiante::setLocalidad);
        setIfHasText(request.getProvincia(), estudiante::setProvincia);
        setIfHasText(request.getTelCelular(), estudiante::setTelCelular);
        setIfHasText(request.getTelFijo(), estudiante::setTelFijo);
    }

    private static void setIfHasText(String value, java.util.function.Consumer<String> setter) {
        if (hasText(value)) {
            setter.accept(trimmedOrNull(value));
        }
    }

    private Usuario findUsuarioByEmailOrThrow(String email) {
        return usuarioMapper.findByEmail(email)
                .orElseThrow(() -> new EstudianteServiceException(USUARIO_NO_ENCONTRADO));
    }

    private static void validateEsEstudiante(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getNombre() == null || !"ESTUDIANTE".equals(usuario.getRol().getNombre())) {
            throw new EstudianteServiceException("El usuario no es un estudiante");
        }
    }

    private static String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean shouldUpdateEmail(String newEmail, String currentEmail) {
        return hasText(newEmail) && !newEmail.equals(currentEmail);
    }

    private void assertEmailNoUsado(String newEmail) {
        if (usuarioMapper.findByEmail(newEmail).isPresent()) {
            throw new EstudianteServiceException("Ya existe un usuario con ese email");
        }
    }

    private static void updateEmail(Usuario usuario, String newEmail) {
        usuario.setEmail(newEmail);
        usuario.setUsername(newEmail); // Mantener consistencia
    }

    private void updateEmailEnEstudiante(Integer idUsuario, String newEmail) {
        estudianteMapper.findByUsuarioId(idUsuario).ifPresent(estudiante -> {
            estudiante.setEmail(newEmail);
            estudianteMapper.update(estudiante);
        });
    }

    private static void validatePassword(String newPassword) {
        if (!newPassword.matches("^(?=.*[a-z])(?=.*\\d).{8,}$")) {
            throw new EstudianteServiceException("La contraseña debe tener al menos 8 caracteres, una letra minúscula y un número");
        }
    }
}