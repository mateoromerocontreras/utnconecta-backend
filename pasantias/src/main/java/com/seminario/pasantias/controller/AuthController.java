package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.LoginRequest;
import com.seminario.pasantias.dto.AuthResponse;
import com.seminario.pasantias.dto.EstudianteRegisterRequest;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.security.JwtUtil;
import com.seminario.pasantias.service.UsuarioService;
import com.seminario.pasantias.service.EstudianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/iniciarSesion")
    public ResponseEntity<AuthResponse> iniciarSesion(@RequestBody LoginRequest request) {
        try {
            System.out.println("=== DEBUG LOGIN ===");
            System.out.println("Email recibido: " + request.getEmail());
            System.out.println("Password recibido: " + request.getPassword());
            
            // Validaciones básicas
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El email es obligatorio"));
            }
            
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "La contraseña es obligatoria"));
            }

            // Buscar usuario por email
            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(request.getEmail());
            System.out.println("Usuario encontrado: " + usuarioOpt.isPresent());
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "Credenciales inválidas"));
            }

            Usuario usuario = usuarioOpt.get();
            System.out.println("Username: " + usuario.getUsername());
            System.out.println("Email: " + usuario.getEmail());
            System.out.println("Activo: " + usuario.getActivo());
            System.out.println("Rol: " + (usuario.getRol() != null ? usuario.getRol().getNombre() : "NULL"));
            
            // Verificar contraseña
            boolean passwordMatch = usuarioService.verifyPassword(request.getPassword(), usuario.getPassword());
            System.out.println("Password match: " + passwordMatch);
            System.out.println("Password hash stored: " + usuario.getPassword());
            
            if (!passwordMatch) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "Credenciales inválidas"));
            }

            // Verificar que el usuario esté activo
            if (!usuario.getActivo()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "Usuario inactivo"));
            }

            // Autenticar (Spring Security usa username, pero podemos usar email como username)
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario.getUsername(), request.getPassword())
            );

            // Generar token
            String roleName = usuario.getRol() != null ? usuario.getRol().getNombre() : "USER";
            String token = jwtUtil.generateToken(usuario.getUsername(), roleName);
            
            // Incluir estado de verificación de email
            Boolean emailVerificado = usuario.getEmailVerificado() != null ? usuario.getEmailVerificado() : false;

            return ResponseEntity.ok(new AuthResponse(token, usuario.getUsername(), 
                                                    usuario.getEmail(), roleName, emailVerificado));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, null, null, "Error en la autenticación: " + e.getMessage()));
        }
    }
    @Operation(summary = "Endpoint segurizado que permite buscar datos sobre el usuario registrado")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "Usuario no encontrado"));
            }
            
            Usuario usuario = usuarioOpt.get();
            String roleName = usuario.getRol() != null ? usuario.getRol().getNombre() : null;
            Boolean emailVerificado = usuario.getEmailVerificado() != null ? usuario.getEmailVerificado() : false;
            return ResponseEntity.ok(new AuthResponse(null, usuario.getUsername(), 
                                                    usuario.getEmail(), roleName, emailVerificado));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, null, null, "Error al obtener usuario: " + e.getMessage()));
        }
    }

    @PostMapping("/cerrarSesion")
    public GenericResponse cerrarSesion() {
        GenericResponse response = new GenericResponse();
        
        try {
            // Limpiar el contexto de seguridad actual
            SecurityContextHolder.clearContext();
            
            // Respuesta exitosa
            response.setCode(0);
            response.setMessage(null);
            return response;
            
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("Error al cerrar sesión: " + e.getMessage());
            return response;
        }
    }
    
    @PostMapping("/registrarEstudiante")
    public ResponseEntity<AuthResponse> registrarEstudiante(@RequestBody EstudianteRegisterRequest request) {
        try {
            // Validaciones básicas
            if (request.getNombre() == null || request.getNombre().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El nombre es obligatorio"));
            }
            
            if (request.getApellido() == null || request.getApellido().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El apellido es obligatorio"));
            }
            
            if (request.getDni() == null || request.getDni().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El DNI es obligatorio"));
            }
            
            if (request.getTelCelular() == null || request.getTelCelular().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El teléfono celular es obligatorio"));
            }
            
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El email es obligatorio"));
            }
            
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "La contraseña es obligatoria"));
            }
            
            // Validación de seguridad de contraseña: al menos 8 caracteres, 1 minúscula y 1 número
            String password = request.getPassword();
            if (!password.matches("^(?=.*[a-z])(?=.*\\d).{8,}$")) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, 
                        "La contraseña debe tener al menos 8 caracteres, una letra minúscula y un número"));
            }
            
            // Verificar si el email ya existe
            Optional<Usuario> usuarioExistente = usuarioService.findByEmail(request.getEmail());
            if (usuarioExistente.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El email ya está registrado"));
            }
            
            // Crear usuario
            Usuario usuario = usuarioService.createUsuario(request.getEmail(), request.getEmail(), 
                                                          request.getPassword(), "ESTUDIANTE");
            
            // Crear perfil de estudiante con datos básicos
            estudianteService.createEstudianteBasico(
                request.getNombre(),
                request.getApellido(),
                request.getDni(),
                request.getTelCelular(),
                request.getEmail(),
                usuario.getIdUsuario()
            );
            
            // Generar token JWT
            String token = jwtUtil.generateToken(usuario.getUsername(), "ESTUDIANTE");
            
            // Retornar respuesta con token e información de verificación
            Boolean emailVerificado = usuario.getEmailVerificado() != null ? usuario.getEmailVerificado() : false;
            String message = emailVerificado ? null : "Por favor verifica tu email. Revisa tu bandeja de entrada.";
            
            AuthResponse response = new AuthResponse(token, usuario.getUsername(), 
                                                     usuario.getEmail(), "ESTUDIANTE", emailVerificado);
            response.setMessage(message);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, null, null, 
                    "Error al registrar estudiante: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint para confirmar la cuenta mediante token de verificación
     * 
     * @param token Token de verificación recibido por email
     * @return Respuesta con código de éxito o error
     */
    @PostMapping("/confirmar-cuenta")
    public ResponseEntity<Map<String, Object>> confirmarCuenta(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            usuarioService.verificarEmail(token);
            response.put("codigo", 0);
            response.put("mensaje", "Cuenta verificada exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("codigo", -1);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("codigo", -1);
            response.put("mensaje", "Error al verificar la cuenta: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}