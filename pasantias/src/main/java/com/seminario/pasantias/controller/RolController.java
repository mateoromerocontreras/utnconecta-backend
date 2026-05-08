package com.seminario.pasantias.controller;

import com.seminario.pasantias.entity.Rol;
import com.seminario.pasantias.dto.RolRequest;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @PostMapping("/registrarRol")
    public GenericResponse registrarRol(@RequestBody RolRequest request) {
        GenericResponse response = new GenericResponse();
        
        if (request.getNombre() == null || request.getNombre().isEmpty()) {
            response.setCode(-1);
            response.setMessage("El nombre es obligatorio");
            return response;
        }
        
        try {
            Rol rol = new Rol(null, request.getNombre(), request.getDescripcion(), null, null);
            rolService.createRol(rol);
            response.setCode(0);
            response.setMessage(null);
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GetMapping("/consultarRol")
    public List<String> consultarRol(@RequestParam(required = false) String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            List<Rol> roles = rolService.getAllRoles();
            return roles.stream()
                    .map(Rol::getNombre)
                    .toList();
        } else {
            Optional<Rol> rol = rolService.getRolByNombre(nombre);
            return rol.map(r -> List.of(r.getNombre()))
                    .orElse(List.of());
        }
    }

    @PostMapping("/modificarRol")
    public GenericResponse modificarRol(@RequestBody RolRequest request) {
        GenericResponse response = new GenericResponse();
        
        if (request.getId() == null || request.getNombre() == null || request.getNombre().isEmpty()) {
            response.setCode(-1);
            response.setMessage("El id y el nombre son obligatorios");
            return response;
        }
        
        try {
            Rol rol = new Rol(request.getId(), request.getNombre(), request.getDescripcion(), null, null);
            rolService.updateRol(rol);
            response.setCode(0);
            response.setMessage(null);
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PostMapping("/eliminarRol")
    public GenericResponse eliminarRol(@RequestBody RolRequest request) {
        GenericResponse response = new GenericResponse();
        
        if (request.getNombre() == null || request.getNombre().isEmpty()) {
            response.setCode(-1);
            response.setMessage("El nombre es obligatorio");
            return response;
        }
        
        try {
            rolService.deleteRolByNombre(request.getNombre());
            response.setCode(0);
            response.setMessage(null);
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GetMapping
    public List<Rol> getAllRoles() {
        return rolService.getAllRoles();
    }

    @GetMapping("/{id}")
    public Rol getRolById(@PathVariable Integer id) {
        return rolService.getRolById(id).orElse(null);
    }
}