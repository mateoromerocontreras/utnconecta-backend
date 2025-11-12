package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.request.ActualizarEstadoPasantiaDTO;
import com.seminario.pasantias.dto.request.PasantiaRequestDTO;
import com.seminario.pasantias.dto.response.PasantiaDetalleDTO;
import com.seminario.pasantias.dto.response.PasantiaResponseDTO;
import com.seminario.pasantias.entity.EstadoPasantia;
import com.seminario.pasantias.security.SecurityService;
import com.seminario.pasantias.service.PasantiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pasantias")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"},
             allowedHeaders = "*")
public class PasantiaController {

    @Autowired
    private PasantiaService pasantiaService;

    @Autowired
    private SecurityService securityService;

    @GetMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<PasantiaResponseDTO>> getAllPasantias() {
        List<PasantiaResponseDTO> pasantias = pasantiaService.obtenerTodasLasPasantias();
        return ResponseEntity.ok()
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(pasantias);
    }

    @GetMapping("/publicadas")
    public ResponseEntity<List<PasantiaResponseDTO>> getPasantiasPublicadas() {
        List<PasantiaResponseDTO> pasantias = pasantiaService.obtenerPasantiasPublicadas();
        return ResponseEntity.ok()
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(pasantias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPasantiaById(@PathVariable Integer id) {
        try {
            PasantiaDetalleDTO pasantia = pasantiaService.obtenerPasantiaPorId(id);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(pasantia);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", "Error al obtener la pasantía: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarPasantia(@Valid @RequestBody PasantiaRequestDTO request) {
        try {
            // Validar que el usuario autenticado tiene permiso para crear pasantía para esta empresa
            securityService.validarPermisoCrearPasantia(request.getIdEmpresa());
            
            PasantiaResponseDTO pasantia = pasantiaService.crearPasantia(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Pasantía registrada exitosamente");
            response.put("data", pasantia);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(response);
        } catch (SecurityException | AccessDeniedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", "Error al registrar la pasantía: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobarPasantia(@PathVariable Integer id) {
        try {
            // Validar que el usuario es ADMINISTRADOR
            securityService.validarEsAdministrador();
            
            // Crear DTO para actualizar estado a PUBLICADA
            ActualizarEstadoPasantiaDTO estadoDTO = new ActualizarEstadoPasantiaDTO();
            estadoDTO.setEstado(EstadoPasantia.PUBLICADA);
            
            // Actualizar el estado de la pasantía
            PasantiaResponseDTO pasantia = pasantiaService.actualizarEstado(id, estadoDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Pasantía aprobada y publicada exitosamente");
            response.put("data", pasantia);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(response);
        } catch (SecurityException | AccessDeniedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", "Error al aprobar la pasantía: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarPasantia(@PathVariable Integer id) {
        try {
            // Validar que el usuario es ADMINISTRADOR
            securityService.validarEsAdministrador();
            
            // Crear DTO para actualizar estado a FINALIZADA
            ActualizarEstadoPasantiaDTO estadoDTO = new ActualizarEstadoPasantiaDTO();
            estadoDTO.setEstado(EstadoPasantia.FINALIZADA);
            
            // Actualizar el estado de la pasantía
            PasantiaResponseDTO pasantia = pasantiaService.actualizarEstado(id, estadoDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Pasantía finalizada exitosamente");
            response.put("data", pasantia);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(response);
        } catch (SecurityException | AccessDeniedException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", "Error al finalizar la pasantía: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

