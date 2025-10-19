package com.seminario.pasantias.service;

import com.seminario.pasantias.entity.Estudiante;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.dto.EstudianteUpdateRequest;
import com.seminario.pasantias.dto.EstudianteUpdateProfileRequest;
import com.seminario.pasantias.dto.EstudianteBasicResponse;
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
            throw new RuntimeException("Usuario no encontrado");
        }
        
        // Verificar que no existe ya un estudiante para este usuario
        Optional<Estudiante> estudianteExistente = estudianteMapper.findByUsuarioId(idUsuario);
        if (estudianteExistente.isPresent()) {
            throw new RuntimeException("Ya existe un perfil de estudiante para este usuario");
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
            throw new RuntimeException("Usuario no encontrado");
        }
        
        // Verificar que no existe ya un estudiante para este usuario
        Optional<Estudiante> estudianteExistente = estudianteMapper.findByUsuarioId(idUsuario);
        if (estudianteExistente.isPresent()) {
            throw new RuntimeException("Ya existe un perfil de estudiante para este usuario");
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
            throw new RuntimeException("Perfil de estudiante no encontrado para el usuario");
        }
        
        Estudiante estudiante = estudianteOpt.get();
        
        // Actualizar campos si se proporcionan
        if (request.getDni() != null && !request.getDni().isEmpty()) {
            estudiante.setDni(request.getDni());
        }
        
        if (request.getApellido() != null && !request.getApellido().isEmpty()) {
            estudiante.setApellido(request.getApellido());
        }
        
        if (request.getNombre() != null && !request.getNombre().isEmpty()) {
            estudiante.setNombre(request.getNombre());
        }
        
        if (request.getEspecialidad() != null && !request.getEspecialidad().isEmpty()) {
            estudiante.setEspecialidad(request.getEspecialidad());
        }
        
        if (request.getNroLegajo() != null && !request.getNroLegajo().isEmpty()) {
            estudiante.setNroLegajo(request.getNroLegajo());
        }
        
        if (request.getCalle() != null && !request.getCalle().isEmpty()) {
            estudiante.setCalle(request.getCalle());
        }
        
        if (request.getNroCalle() != null) {
            estudiante.setNroCalle(request.getNroCalle());
        }
        
        if (request.getBarrio() != null && !request.getBarrio().isEmpty()) {
            estudiante.setBarrio(request.getBarrio());
        }
        
        if (request.getLocalidad() != null && !request.getLocalidad().isEmpty()) {
            estudiante.setLocalidad(request.getLocalidad());
        }
        
        if (request.getProvincia() != null && !request.getProvincia().isEmpty()) {
            estudiante.setProvincia(request.getProvincia());
        }
        
        if (request.getTelCelular() != null && !request.getTelCelular().isEmpty()) {
            estudiante.setTelCelular(request.getTelCelular());
        }
        
        if (request.getTelFijo() != null && !request.getTelFijo().isEmpty()) {
            estudiante.setTelFijo(request.getTelFijo());
        }
        
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
        return new EstudianteBasicResponse(
            estudiante.getIdEstudiante(),
            estudiante.getDni(),
            estudiante.getApellido(),
            estudiante.getNombre(),
            estudiante.getEspecialidad(),
            estudiante.getNroLegajo(),
            estudiante.getCalle(),
            estudiante.getNroCalle(),
            estudiante.getBarrio(),
            estudiante.getLocalidad(),
            estudiante.getProvincia(),
            estudiante.getEmail(),
            estudiante.getTelCelular(),
            estudiante.getTelFijo(),
            estudiante.getActivo(),
            estudiante.getFechaCreacion()
        );
    }
    
    // Método para obtener todos los estudiantes como respuesta básica
    public List<EstudianteBasicResponse> getAllEstudiantesBasic() {
        List<Estudiante> estudiantes = estudianteMapper.findAllActive();
        return estudiantes.stream()
                .map(this::toBasicResponse)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Método para buscar estudiante por nombre como respuesta básica
    public Optional<EstudianteBasicResponse> getEstudianteByNombreBasic(String nombre) {
        Optional<Estudiante> estudiante = estudianteMapper.findByNombre(nombre);
        return estudiante.map(this::toBasicResponse);
    }
    
    // Método para actualizar email y/o contraseña del estudiante
    public void updateEstudianteProfile(String currentEmail, EstudianteUpdateProfileRequest request) {
        // Buscar usuario por email actual
        Optional<Usuario> usuarioOpt = usuarioMapper.findByEmail(currentEmail);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar que es un estudiante
        if (usuario.getRol() == null || !"ESTUDIANTE".equals(usuario.getRol().getNombre())) {
            throw new RuntimeException("El usuario no es un estudiante");
        }
        
        boolean needsUpdate = false;
        
        // Si se proporciona un nuevo email
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            
            // Verificar que el nuevo email no sea el mismo que el actual
            if (!newEmail.equals(currentEmail)) {
                // Verificar que no exista otro usuario con ese email
                Optional<Usuario> existingUser = usuarioMapper.findByEmail(newEmail);
                if (existingUser.isPresent()) {
                    throw new RuntimeException("Ya existe un usuario con ese email");
                }
                
                // Actualizar email en Usuario
                usuario.setEmail(newEmail);
                usuario.setUsername(newEmail); // Mantener consistencia
                needsUpdate = true;
                
                // También actualizar el email en la tabla Estudiante
                Optional<Estudiante> estudianteOpt = estudianteMapper.findByUsuarioId(usuario.getIdUsuario());
                if (estudianteOpt.isPresent()) {
                    Estudiante estudiante = estudianteOpt.get();
                    estudiante.setEmail(newEmail);
                    estudianteMapper.update(estudiante);
                }
            }
        }
        
        // Si se proporciona una nueva contraseña
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            String newPassword = request.getPassword().trim();
            
            // Validación de seguridad de contraseña: al menos 8 caracteres, 1 minúscula y 1 número
            if (!newPassword.matches("^(?=.*[a-z])(?=.*\\d).{8,}$")) {
                throw new RuntimeException("La contraseña debe tener al menos 8 caracteres, una letra minúscula y un número");
            }
            
            // Encriptar nueva contraseña
            usuario.setPassword(passwordEncoder.encode(newPassword));
            needsUpdate = true;
        }
        
        // Actualizar usuario si hay cambios
        if (needsUpdate) {
            usuarioMapper.update(usuario);
        }
    }
}