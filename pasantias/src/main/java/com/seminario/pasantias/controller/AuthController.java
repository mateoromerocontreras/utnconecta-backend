package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.LoginRequest;
import com.seminario.pasantias.dto.AuthResponse;
import com.seminario.pasantias.dto.EstudianteRegisterRequest;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.security.JwtUtil;
import com.seminario.pasantias.service.UsuarioService;
import com.seminario.pasantias.service.EstudianteService;
import com.seminario.pasantias.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/iniciarSesion")
    public ResponseEntity<AuthResponse> iniciarSesion(@RequestBody LoginRequest request) {
        try {
            log.debug("Login attempt. email={}", request.getEmail());
            
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
            log.debug("Usuario encontrado: {}", usuarioOpt.isPresent());
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "Credenciales inválidas"));
            }

            Usuario usuario = usuarioOpt.get();
            log.debug("Usuario login. username={}, activo={}, rol={}",
                    usuario.getUsername(),
                    usuario.getActivo(),
                    usuario.getRol() != null ? usuario.getRol().getNombre() : "NULL");
            
            // Verificar contraseña
            boolean passwordMatch = usuarioService.verifyPassword(request.getPassword(), usuario.getPassword());
            log.debug("Password match: {}", passwordMatch);
            
            if (!passwordMatch) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "Credenciales inválidas"));
            }

            // Verificar que el usuario esta activo
            if (!usuario.getActivo()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El usuario aun no confirmo su email"));
            }

            // Autenticar (Spring Security usa username, pero podemos usar email como username)
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario.getUsername(), request.getPassword())
            );

            // Generar token
            String roleName = usuario.getRol() != null ? usuario.getRol().getNombre() : "USER";
            String token = jwtUtil.generateToken(usuario.getUsername(), roleName);

            return ResponseEntity.ok(new AuthResponse(token, usuario.getUsername(), 
                                                    usuario.getEmail(), roleName));

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
            return ResponseEntity.ok(new AuthResponse(null, usuario.getUsername(), 
                                                    usuario.getEmail(), roleName, null));
            
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
                    .body(new AuthResponse(null, null, null, null, "El telefono celular es obligatorio"));
            }

            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El email es obligatorio"));
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "La contrasena es obligatoria"));
            }

            String password = request.getPassword();
            if (!password.matches("^(?=.*[a-z])(?=.*\\d).{8,}$")) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null,
                        "La contrasena debe tener al menos 8 caracteres, una letra minuscula y un numero"));
            }

            Optional<Usuario> usuarioExistente = usuarioService.findByEmail(request.getEmail());
            if (usuarioExistente.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, null, "El email ya esta registrado"));
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

            emailVerificationService.crearYEnviarToken(usuario);

            return ResponseEntity.ok(new AuthResponse(
                null,
                usuario.getUsername(),
                usuario.getEmail(),
                "ESTUDIANTE",
                "Registro exitoso. Revisa tu correo para confirmar la cuenta."
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(null, null, null, null,
                    "Error al registrar estudiante: " + e.getMessage()));
        }
    }

    @GetMapping("/confirmar")
    public ResponseEntity<GenericResponse> confirmarEmail(@RequestParam String token) {
        GenericResponse response = new GenericResponse();
        try {
            emailVerificationService.confirmarToken(token);
            response.setCode(0);
            response.setMessage("Cuenta verificada correctamente. Ya podes iniciar sesion.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage("No se pudo verificar la cuenta: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

