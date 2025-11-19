package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.EstudianteRegisterRequest;
import com.seminario.pasantias.dto.EstudianteUpdateRequest;
import com.seminario.pasantias.dto.EstudianteUpdateProfileRequest;
import com.seminario.pasantias.dto.EstudianteBasicResponse;
import com.seminario.pasantias.entity.Estudiante;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.EstudianteService;
import com.seminario.pasantias.service.UsuarioService;
import com.seminario.pasantias.service.EmailVerificationService;
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

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/crearEstudiante")
    public GenericResponse crearEstudiante(@RequestBody EstudianteRegisterRequest request) {
        GenericResponse response = new GenericResponse();

        try {
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
                response.setMessage("El telefono celular es obligatorio");
                return response;
            }

            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                response.setCode(-1);
                response.setMessage("El email es obligatorio");
                return response;
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                response.setCode(-1);
                response.setMessage("La contrasena es obligatoria");
                return response;
            }

            String password = request.getPassword();
            if (!password.matches("^(?=.*[a-z])(?=.*\\d).{8,}$")) {
                response.setCode(-1);
                response.setMessage("La contrasena debe tener al menos 8 caracteres, una letra minuscula y un numero");
                return response;
            }

            Optional<Usuario> usuarioExistente = usuarioService.findByEmail(request.getEmail());
            if (usuarioExistente.isPresent()) {
                response.setCode(-1);
                response.setMessage("El email ya esta registrado");
                return response;
            }

            Usuario usuario = usuarioService.createUsuario(
                request.getEmail(),
                request.getEmail(),
                request.getPassword(),
                "ESTUDIANTE",
                false
            );

            estudianteService.createEstudianteBasico(
                request.getNombre(),
                request.getApellido(),
                request.getDni(),
                request.getTelCelular(),
                request.getEmail(),
                usuario.getIdUsuario()
            );

            emailVerificationService.enviarCorreoDeVerificacion(usuario);

            response.setCode(0);
            response.setMessage("Se envio un email de verificacion para activar la cuenta");
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

    @GetMapping("/getEstudiantes")
    public Object getEstudiantes(@RequestParam(required = false) String nombre) {
        try {
            if (nombre != null && !nombre.trim().isEmpty()) {
                // Buscar estudiante específico por nombre
                Optional<EstudianteBasicResponse> estudiante = estudianteService.getEstudianteByNombreBasic(nombre.trim());
                if (estudiante.isPresent()) {
                    return estudiante.get();
                } else {
                    return Optional.empty();
                }
            } else {
                // Devolver todos los estudiantes
                List<EstudianteBasicResponse> estudiantes = estudianteService.getAllEstudiantesBasic();
                return estudiantes;
            }
        } catch (Exception e) {
            return "Error al obtener estudiantes: " + e.getMessage();
        }
    }

    @PutMapping("/updateEstudiante")
    public GenericResponse updateEstudiante(@RequestBody EstudianteUpdateProfileRequest request, 
                                          @RequestParam String currentEmail) {
        GenericResponse response = new GenericResponse();
        
        try {
            // Validaciones básicas
            if (currentEmail == null || currentEmail.trim().isEmpty()) {
                response.setCode(-1);
                response.setMessage("El email actual es obligatorio");
                return response;
            }
            
            // Verificar que al menos uno de los campos está presente
            if ((request.getEmail() == null || request.getEmail().trim().isEmpty()) &&
                (request.getPassword() == null || request.getPassword().trim().isEmpty())) {
                response.setCode(-1);
                response.setMessage("Debe proporcionar al menos un campo para actualizar (email o contraseña)");
                return response;
            }
            
            // Validar formato de email si se proporciona
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                String newEmail = request.getEmail().trim();
                if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    response.setCode(-1);
                    response.setMessage("El formato del email es inválido");
                    return response;
                }
            }
            
            // Actualizar perfil del estudiante
            estudianteService.updateEstudianteProfile(currentEmail.trim(), request);
            
            response.setCode(0);
            response.setMessage("Perfil actualizado exitosamente");
            return response;
            
        } catch (RuntimeException e) {
            response.setCode(-1);
            response.setMessage(e.getMessage());
            return response;
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("Error al actualizar perfil: " + e.getMessage());
            return response;
        }
    }
}

