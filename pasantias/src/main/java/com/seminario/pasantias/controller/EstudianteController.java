package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.EstudianteRegisterRequest;
import com.seminario.pasantias.dto.EstudianteUpdateRequest;
import com.seminario.pasantias.entity.Estudiante;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.EstudianteService;
import com.seminario.pasantias.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/crearEstudiante")
    public GenericResponse crearEstudiante(@RequestBody EstudianteRegisterRequest request) {
        GenericResponse response = new GenericResponse();
        
        try {
            // Validaciones básicas
            if (request.getNombre() == null || request.getNombre().isEmpty()) {
                response.setCode(-1);
                response.setMessage("El nombre es obligatorio");
                return response;
            }
            
            if (request.getApellido() == null || request.getApellido().isEmpty()) {
                response.setCode(-1);
                response.setMessage("El apellido es obligatorio");
                return response;
            }
            
            if (request.getDni() == null || request.getDni().isEmpty()) {
                response.setCode(-1);
                response.setMessage("El DNI es obligatorio");
                return response;
            }
            
            if (request.getTelCelular() == null || request.getTelCelular().isEmpty()) {
                response.setCode(-1);
                response.setMessage("El teléfono celular es obligatorio");
                return response;
            }
            
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                response.setCode(-1);
                response.setMessage("El email es obligatorio");
                return response;
            }
            
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                response.setCode(-1);
                response.setMessage("La contraseña es obligatoria");
                return response;
            }
            
            // Validación de seguridad de contraseña: al menos 8 caracteres, 1 minúscula y 1 número
            String password = request.getPassword();
            if (!password.matches("^(?=.*[a-z])(?=.*\\d).{8,}$")) {
                response.setCode(-1);
                response.setMessage("La contraseña debe tener al menos 8 caracteres, una letra minúscula y un número");
                return response;
            }
            
            // Verificar si el email ya existe
            Optional<Usuario> usuarioExistente = usuarioService.findByEmail(request.getEmail());
            if (usuarioExistente.isPresent()) {
                response.setCode(-1);
                response.setMessage("El email ya está registrado");
                return response;
            }
            
            // Crear usuario directamente usando UsuarioService
            Usuario usuario = usuarioService.createUsuario(request.getEmail(), request.getEmail(), request.getPassword(), "ESTUDIANTE");
            
            // Crear el perfil de estudiante con datos básicos
            estudianteService.createEstudianteBasico(
                request.getNombre(),
                request.getApellido(),
                request.getDni(),
                request.getTelCelular(),
                request.getEmail(),
                usuario.getIdUsuario()
            );
            
            response.setCode(0);
            response.setMessage(null);
            return response;

        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("Error al crear estudiante: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/completarPerfil")
    public GenericResponse completarPerfil(@RequestBody EstudianteUpdateRequest request, 
                                         @RequestParam String email) {
        GenericResponse response = new GenericResponse();
        
        try {
            // Buscar usuario por email
            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                response.setCode(-1);
                response.setMessage("Usuario no encontrado");
                return response;
            }
            
            // Verificar que el usuario tenga rol de estudiante
            if (usuarioOpt.get().getRol() == null || 
                !"ESTUDIANTE".equals(usuarioOpt.get().getRol().getNombre())) {
                response.setCode(-1);
                response.setMessage("El usuario no tiene rol de estudiante");
                return response;
            }
            
            // Actualizar perfil del estudiante
            estudianteService.updateEstudiante(usuarioOpt.get().getIdUsuario(), request);
            
            response.setCode(0);
            response.setMessage("Perfil completado exitosamente");
            return response;
            
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("Error al completar perfil: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/perfil")
    public Object obtenerPerfil(@RequestParam String email) {
        try {
            // Buscar usuario por email
            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
            if (usuarioOpt.isEmpty()) {
                return "Usuario no encontrado";
            }
            
            // Buscar perfil de estudiante
            Optional<Estudiante> estudianteOpt = estudianteService.findByUsuarioId(usuarioOpt.get().getIdUsuario());
            if (estudianteOpt.isEmpty()) {
                return "Perfil de estudiante no encontrado";
            }
            
            return estudianteOpt.get();
            
        } catch (Exception e) {
            return "Error al obtener perfil: " + e.getMessage();
        }
    }

    @GetMapping("/listarEstudiantes")
    public Object listarEstudiantes() {
        try {
            List<Estudiante> estudiantes = estudianteService.findAllActive();
            return estudiantes;
        } catch (Exception e) {
            return "Error al listar estudiantes: " + e.getMessage();
        }
    }
}