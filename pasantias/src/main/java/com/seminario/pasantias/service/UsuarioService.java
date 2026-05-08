package com.seminario.pasantias.service;

import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.entity.Rol;
import com.seminario.pasantias.dto.UpdateUsuarioRequest;
import com.seminario.pasantias.persistence.UsuarioMapper;
import com.seminario.pasantias.persistence.RolMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private static final String NO_ENCONTRADO_SUFFIX = " no encontrado";

    public static class UsuarioServiceException extends RuntimeException {
        public UsuarioServiceException(String message) {
            super(message);
        }
    }

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private RolMapper rolMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Usuario> findByUsername(String username) {
        return usuarioMapper.findByUsername(username);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioMapper.findByEmail(email);
    }

    public List<Usuario> findAllActive() {
        return usuarioMapper.findAllActive();
    }

    public Usuario createUsuario(String username, String email, String password, String rolNombre) {
        return createUsuario(username, email, password, rolNombre, true);
    }

    public Usuario createUsuario(String username, String email, String password, String rolNombre, boolean activoInicial) {
        if (usuarioMapper.findByUsername(username).isPresent()) {
            throw new UsuarioServiceException("El username ya existe");
        }

        // Buscar el rol por nombre
        log.debug("Buscando rol: {}", rolNombre);
        Optional<Rol> rolOpt = rolMapper.findByNombre(rolNombre);
        if (rolOpt.isEmpty()) {
            log.debug("Rol no encontrado: {}", rolNombre);
            throw new UsuarioServiceException("El rol especificado no existe: " + rolNombre);
        }

        log.debug("Rol encontrado: {} con ID: {}", rolOpt.get().getNombre(), rolOpt.get().getIdRol());
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setIdRol(rolOpt.get().getIdRol());
        usuario.setActivo(activoInicial);
        usuario.setFechaCreacion(LocalDateTime.now());

        usuarioMapper.insert(usuario);
        return usuario;
    }

    public void deactivateUsuario(Integer id) {
        usuarioMapper.deactivate(id);
    }

    public void activateUsuario(Integer id) {
        usuarioMapper.activate(id);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void updateUsuario(UpdateUsuarioRequest request) {
        Usuario usuario = findUsuarioToUpdate(request);
        applyUpdates(usuario, request);
        usuarioMapper.update(usuario);
    }

    public void deleteUsuarioByNombre(String nombre) {
        // Verificar que el usuario existe
        Optional<Usuario> usuarioOpt = usuarioMapper.findByUsername(nombre);
        if (usuarioOpt.isEmpty()) {
            throw new UsuarioServiceException("Usuario con nombre " + nombre + NO_ENCONTRADO_SUFFIX);
        }
        
        // Eliminar usuario de la base de datos
        usuarioMapper.deleteByUsername(nombre);
    }

    private Usuario findUsuarioToUpdate(UpdateUsuarioRequest request) {
        String usernameLookup = firstNonBlank(request.getIdUsuario(), request.getNombre());
        if (usernameLookup == null) {
            throw new UsuarioServiceException("Debe proporcionar al menos idUsuario o nombre");
        }

        Optional<Usuario> usuarioOpt = usuarioMapper.findByUsername(usernameLookup);
        if (usuarioOpt.isEmpty()) {
            String label = request.getIdUsuario() != null && !request.getIdUsuario().isEmpty() ? "idUsuario" : "nombre";
            throw new UsuarioServiceException("Usuario con " + label + " " + usernameLookup + NO_ENCONTRADO_SUFFIX);
        }
        return usuarioOpt.get();
    }

    private void applyUpdates(Usuario usuario, UpdateUsuarioRequest request) {
        if (hasText(request.getNombre())) {
            usuario.setUsername(request.getNombre());
        }

        if (hasText(request.getEmail())) {
            usuario.setEmail(request.getEmail());
        }

        if (hasText(request.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (hasText(request.getRol())) {
            Optional<Rol> rolOpt = rolMapper.findByNombre(request.getRol());
            if (rolOpt.isEmpty()) {
                throw new UsuarioServiceException("El rol especificado no existe");
            }
            usuario.setIdRol(rolOpt.get().getIdRol());
        }

        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isEmpty();
    }

    private static String firstNonBlank(String a, String b) {
        if (hasText(a)) return a;
        if (hasText(b)) return b;
        return null;
    }
}
