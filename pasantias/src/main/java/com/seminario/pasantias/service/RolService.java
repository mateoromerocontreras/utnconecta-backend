package com.seminario.pasantias.service;

import com.seminario.pasantias.entity.Rol;
import com.seminario.pasantias.persistence.RolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    public static class RolServiceException extends RuntimeException {
        public RolServiceException(String message) {
            super(message);
        }
    }

    @Autowired
    private RolMapper rolMapper;

    public List<Rol> getAllRoles() {
        return rolMapper.findAllActive();
    }

    public Optional<Rol> getRolById(Integer id) {
        return rolMapper.findById(id);
    }

    public Optional<Rol> getRolByNombre(String nombre) {
        return rolMapper.findByNombre(nombre);
    }

    public void createRol(Rol rol) {
        if (rolMapper.findByNombre(rol.getNombre()).isPresent()) {
            throw new RolServiceException("Ya existe un rol con ese nombre");
        }
        
        rol.setActivo(true);
        rol.setFechaCreacion(LocalDateTime.now());
        rolMapper.insert(rol);
    }

    public void updateRol(Rol rol) {
        Optional<Rol> existingRol = rolMapper.findById(rol.getIdRol());
        if (existingRol.isEmpty()) {
            throw new RolServiceException("No se encontró el rol a actualizar");
        }

        // Verificar que no exista otro rol con el mismo nombre
        Optional<Rol> rolConMismoNombre = rolMapper.findByNombre(rol.getNombre());
        if (rolConMismoNombre.isPresent() && !rolConMismoNombre.get().getIdRol().equals(rol.getIdRol())) {
            throw new RolServiceException("Ya existe otro rol con ese nombre");
        }

        rolMapper.update(rol);
    }

    public void deleteRolByNombre(String nombre) {
        Optional<Rol> rol = rolMapper.findByNombre(nombre);
        if (rol.isEmpty()) {
            throw new RolServiceException("No se encontró un rol con ese nombre");
        }
        rolMapper.deactivate(rol.get().getIdRol());
    }

    public void deleteRolById(Integer id) {
        Optional<Rol> rol = rolMapper.findById(id);
        if (rol.isEmpty()) {
            throw new RolServiceException("No se encontró el rol a eliminar");
        }
        rolMapper.deactivate(id);
    }
}