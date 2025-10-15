package com.seminario.pasantias.service;

import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.entity.Rol;
import com.seminario.pasantias.dto.UpdateUsuarioRequest;
import com.seminario.pasantias.persistence.UsuarioMapper;
import com.seminario.pasantias.persistence.RolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

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
        if (usuarioMapper.findByUsername(username).isPresent()) {
            throw new RuntimeException("El username ya existe");
        }

        // Buscar el rol por nombre
        System.out.println("Buscando rol: " + rolNombre);
        Optional<Rol> rolOpt = rolMapper.findByNombre(rolNombre);
        if (rolOpt.isEmpty()) {
            System.out.println("Rol no encontrado: " + rolNombre);
            throw new RuntimeException("El rol especificado no existe: " + rolNombre);
        }

        System.out.println("Rol encontrado: " + rolOpt.get().getNombre() + " con ID: " + rolOpt.get().getIdRol());
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setIdRol(rolOpt.get().getIdRol());
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        usuarioMapper.insert(usuario);
        return usuario;
    }

    public void deactivateUsuario(Integer id) {
        usuarioMapper.deactivate(id);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void updateUsuario(UpdateUsuarioRequest request) {
        Usuario usuario = null;
        
        // Buscar usuario por idUsuario o nombre
        if (request.getIdUsuario() != null && !request.getIdUsuario().isEmpty()) {
            Optional<Usuario> usuarioOpt = usuarioMapper.findByUsername(request.getIdUsuario());
            if (usuarioOpt.isEmpty()) {
                throw new RuntimeException("Usuario con idUsuario " + request.getIdUsuario() + " no encontrado");
            }
            usuario = usuarioOpt.get();
        } else if (request.getNombre() != null && !request.getNombre().isEmpty()) {
            Optional<Usuario> usuarioOpt = usuarioMapper.findByUsername(request.getNombre());
            if (usuarioOpt.isEmpty()) {
                throw new RuntimeException("Usuario con nombre " + request.getNombre() + " no encontrado");
            }
            usuario = usuarioOpt.get();
        } else {
            throw new RuntimeException("Debe proporcionar al menos idUsuario o nombre");
        }
        
        // Actualizar campos si se proporcionan
        if (request.getNombre() != null && !request.getNombre().isEmpty()) {
            usuario.setUsername(request.getNombre());
        }
        
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            usuario.setEmail(request.getEmail());
        }
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getRol() != null && !request.getRol().isEmpty()) {
            Optional<Rol> rolOpt = rolMapper.findByNombre(request.getRol());
            if (rolOpt.isEmpty()) {
                throw new RuntimeException("El rol especificado no existe");
            }
            usuario.setIdRol(rolOpt.get().getIdRol());
        }
        
        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }
        
        // Actualizar en base de datos
        usuarioMapper.update(usuario);
    }

    public void deleteUsuarioByNombre(String nombre) {
        // Verificar que el usuario existe
        Optional<Usuario> usuarioOpt = usuarioMapper.findByUsername(nombre);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario con nombre " + nombre + " no encontrado");
        }
        
        // Eliminar usuario de la base de datos
        usuarioMapper.deleteByUsername(nombre);
    }
}