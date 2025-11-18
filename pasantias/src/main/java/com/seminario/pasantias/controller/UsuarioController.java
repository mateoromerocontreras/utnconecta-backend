package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.RegisterRequest;
import com.seminario.pasantias.dto.UpdateUsuarioRequest;
import com.seminario.pasantias.dto.EliminarUsuarioRequest;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrarUsuario")
    public GenericResponse registrarUsuario(@RequestBody RegisterRequest request) {
        GenericResponse response = new GenericResponse();
        
        try {
            // Validaciones
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                response.setCode(-1);
                response.setMessage("El username es obligatorio");
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
            
            if (request.getRol() == null) {
                response.setCode(-1);
                response.setMessage("El rol es obligatorio");
                return response;
            }

            // Crear usuario
            usuarioService.createUsuario(request.getUsername(), request.getEmail(), request.getPassword(), request.getRol());
            
            response.setCode(0);
            response.setMessage("Usuario creado exitosamente. Por favor verifica tu email para activar tu cuenta.");
            return response;

        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @GetMapping("/consultarUsuario")
    public Object consultarUsuario(@RequestParam(required = false) String nombre) {
        try {
            if (nombre == null || nombre.isEmpty()) {
                // Si no se proporciona nombre, devolver todos los usuarios
                return usuarioService.findAllActive();
            } else {
                // Si se proporciona nombre, buscar usuario específico
                Optional<Usuario> usuarioOpt = usuarioService.findByUsername(nombre);
                if (usuarioOpt.isEmpty()) {
                    return Optional.empty();
                }
                return usuarioOpt.get();
            }
        } catch (Exception e) {
            return "Error al consultar usuario: " + e.getMessage();
        }
    }

    @PostMapping("/actualizarUsuario")
    public GenericResponse actualizarUsuario(@RequestBody UpdateUsuarioRequest request) {
        GenericResponse response = new GenericResponse();
        
        try {
            // Validación: al menos uno de idUsuario o nombre debe venir completo
            if ((request.getIdUsuario() == null || request.getIdUsuario().isEmpty()) && 
                (request.getNombre() == null || request.getNombre().isEmpty())) {
                response.setCode(-1);
                response.setMessage("Debe proporcionar al menos idUsuario o nombre");
                return response;
            }
            
            // Validación de contraseña si se proporciona (8 caracteres, 1 minúscula, 1 mayúscula, 1 número)
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                if (!request.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                    response.setCode(-1);
                    response.setMessage("La contraseña debe tener al menos 8 caracteres, 1 minúscula, 1 mayúscula y 1 número");
                    return response;
                }
            }
            
            // Actualizar usuario
            usuarioService.updateUsuario(request);
            
            response.setCode(0);
            response.setMessage(null);
            return response;
            
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @PostMapping("/desactivarUsuario")
    public GenericResponse desactivarUsuario(@RequestBody UpdateUsuarioRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            if (request.getIdUsuario() == null || request.getIdUsuario().isEmpty()) {
                response.setCode(-1);
                response.setMessage("Debe proporcionar el idUsuario (username) del usuario a desactivar.");
                return response;
            }

            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(request.getIdUsuario());
            if (usuarioOpt.isEmpty()) {
                response.setCode(-1);
                response.setMessage("Usuario no encontrado.");
                return response;
            }

            usuarioService.deactivateUsuario(usuarioOpt.get().getIdUsuario());
            response.setCode(0);
            return response;
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("Error al desactivar usuario: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/eliminarUsuario")
    public GenericResponse eliminarUsuario(@RequestBody EliminarUsuarioRequest request) {
        GenericResponse response = new GenericResponse();
        
        try {
            // Validación de campo obligatorio
            if (request.getNombre() == null || request.getNombre().isEmpty()) {
                response.setCode(-1);
                response.setMessage("El nombre es obligatorio");
                return response;
            }
            
            // Verificar que el usuario existe antes de eliminar
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(request.getNombre());
            if (usuarioOpt.isEmpty()) {
                response.setCode(-1);
                response.setMessage("Usuario no encontrado");
                return response;
            }
            
            // Eliminar usuario de la base de datos
            usuarioService.deleteUsuarioByNombre(request.getNombre());
            
            response.setCode(0);
            response.setMessage(null);
            return response;
            
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("Error al eliminar usuario: " + e.getMessage());
            return response;
        }
    }
}