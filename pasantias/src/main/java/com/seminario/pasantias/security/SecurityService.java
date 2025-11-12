package com.seminario.pasantias.security;

import com.seminario.pasantias.entity.Empresa;
import com.seminario.pasantias.entity.Pasantia;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.persistence.EmpresaMapper;
import com.seminario.pasantias.persistence.PasantiaMapper;
import com.seminario.pasantias.persistence.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para validaciones de seguridad y permisos
 */
@Service
public class SecurityService {

    private final UsuarioMapper usuarioMapper;
    private final EmpresaMapper empresaMapper;
    private final PasantiaMapper pasantiaMapper;

    @Autowired
    public SecurityService(UsuarioMapper usuarioMapper, 
                          EmpresaMapper empresaMapper,
                          PasantiaMapper pasantiaMapper) {
        this.usuarioMapper = usuarioMapper;
        this.empresaMapper = empresaMapper;
        this.pasantiaMapper = pasantiaMapper;
    }

    /**
     * Obtiene el usuario autenticado actual
     */
    public Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No hay usuario autenticado");
        }
        
        String username = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioMapper.findByUsername(username);
        
        if (usuarioOpt.isEmpty()) {
            throw new SecurityException("Usuario no encontrado: " + username);
        }
        
        return usuarioOpt.get();
    }

    /**
     * Verifica si el usuario autenticado tiene un rol específico
     */
    public boolean tieneRol(String nombreRol) {
        Usuario usuario = getUsuarioAutenticado();
        return usuario.getRol() != null && 
               nombreRol.equalsIgnoreCase(usuario.getRol().getNombre());
    }

    /**
     * Verifica si el usuario es ADMINISTRADOR
     */
    public boolean esAdministrador() {
        return tieneRol("ADMINISTRADOR");
    }

    /**
     * Verifica si el usuario es EMPRESA
     */
    public boolean esEmpresa() {
        return tieneRol("EMPRESA");
    }

    /**
     * Verifica si el usuario es ESTUDIANTE
     */
    public boolean esEstudiante() {
        return tieneRol("ESTUDIANTE");
    }

    /**
     * Obtiene la empresa asociada al usuario autenticado
     * @return Empresa si el usuario es de tipo EMPRESA y tiene una empresa asignada
     * @throws SecurityException si el usuario no es EMPRESA o no tiene empresa asignada
     */
    public Empresa getEmpresaDelUsuario() {
        Usuario usuario = getUsuarioAutenticado();
        
        if (!esEmpresa()) {
            throw new SecurityException("El usuario no es de tipo EMPRESA");
        }
        
        // Buscar empresa por idUsuario
        Empresa empresa = empresaMapper.findByIdUsuario(usuario.getIdUsuario());
        
        if (empresa == null) {
            throw new SecurityException("El usuario no tiene una empresa asignada");
        }
        
        return empresa;
    }

    /**
     * Verifica si el usuario autenticado puede crear una pasantía para una empresa específica
     * 
     * Reglas:
     * - ADMINISTRADOR: Puede crear para cualquier empresa
     * - EMPRESA: Solo puede crear para su propia empresa
     * 
     * @param empresaId ID de la empresa para la cual se quiere crear la pasantía
     * @return true si tiene permiso, false en caso contrario
     */
    public boolean puedeCrearPasantiaParaEmpresa(Integer empresaId) {
        // Administrador puede crear para cualquier empresa
        if (esAdministrador()) {
            return true;
        }
        
        // Empresa solo puede crear para su propia empresa
        if (esEmpresa()) {
            Empresa empresa = getEmpresaDelUsuario();
            return empresa.getIdEmpresa().equals(empresaId);
        }
        
        // Otros roles no pueden crear pasantías
        return false;
    }

    /**
     * Verifica si el usuario puede modificar una pasantía
     * 
     * Reglas:
     * - ADMINISTRADOR: Puede modificar cualquier pasantía
     * - EMPRESA: Solo puede modificar pasantías de su empresa
     */
    public boolean puedeModificarPasantia(Integer pasantiaId) {
        // Administrador puede modificar cualquier pasantía
        if (esAdministrador()) {
            return true;
        }
        
        // Empresa solo puede modificar sus propias pasantías
        if (esEmpresa()) {
            Optional<Pasantia> pasantiaOpt = pasantiaMapper.findById(pasantiaId);
            if (pasantiaOpt.isEmpty()) {
                return false;
            }
            
            Pasantia pasantia = pasantiaOpt.get();
            Empresa empresa = getEmpresaDelUsuario();
            
            return pasantia.getEmpresa() != null && 
                   pasantia.getEmpresa().getIdEmpresa().equals(empresa.getIdEmpresa());
        }
        
        return false;
    }

    /**
     * Verifica permisos y lanza excepción si no tiene acceso
     */
    public void validarPermisoCrearPasantia(Integer empresaId) {
        if (!puedeCrearPasantiaParaEmpresa(empresaId)) {
            if (esEmpresa()) {
                Empresa miEmpresa = getEmpresaDelUsuario();
                throw new SecurityException(
                    String.format("No tienes permiso para crear pasantías para la empresa %d. " +
                                "Solo puedes crear pasantías para tu empresa (%d - %s)",
                                empresaId, miEmpresa.getIdEmpresa(), miEmpresa.getNombre())
                );
            } else {
                throw new SecurityException(
                    "No tienes permiso para crear pasantías. Se requiere rol EMPRESA o ADMINISTRADOR"
                );
            }
        }
    }

    /**
     * Verifica permisos y lanza excepción si no tiene acceso para modificar
     */
    public void validarPermisoModificarPasantia(Integer pasantiaId) {
        if (!puedeModificarPasantia(pasantiaId)) {
            throw new SecurityException(
                "No tienes permiso para modificar esta pasantía"
            );
        }
    }

    /**
     * Verifica que el usuario autenticado es ADMINISTRADOR
     * @throws SecurityException si el usuario no es ADMINISTRADOR
     */
    public void validarEsAdministrador() {
        if (!esAdministrador()) {
            throw new SecurityException(
                "Solo los usuarios con rol ADMINISTRADOR pueden realizar esta acción"
            );
        }
    }
}
