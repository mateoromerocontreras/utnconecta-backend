package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.request.ActualizarEstadoPasantiaDTO;
import com.seminario.pasantias.dto.request.PasantiaRequestDTO;
import com.seminario.pasantias.dto.response.PasantiaDetalleDTO;
import com.seminario.pasantias.dto.response.PasantiaResponseDTO;
import com.seminario.pasantias.entity.EstadoPasantia;
import com.seminario.pasantias.security.SecurityService;
import com.seminario.pasantias.service.PasantiaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar las operaciones relacionadas con Pasantías.
 * 
 * <p>Las pasantías tienen solo 2 estados:
 * <ul>
 *   <li><strong>PUBLICADA</strong>: visible para todos, acepta postulaciones</li>
 *   <li><strong>FINALIZADA</strong>: no visible para estudiantes, no acepta postulaciones</li>
 * </ul>
 * 
 * <p>Al crear una pasantía, se publica automáticamente sin necesidad de aprobación.
 * La empresa dueña o el administrador pueden finalizarla manualmente.
 * Las pasantías con fecha de caducidad vencida se finalizan automáticamente.
 */
@RestController
@RequestMapping("/pasantias")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"},
             allowedHeaders = "*")
public class PasantiaController {

    private static final Logger log = LoggerFactory.getLogger(PasantiaController.class);

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_JSON_UTF8 = "application/json;charset=UTF-8";
    private static final String KEY_CODIGO = "codigo";
    private static final String KEY_MENSAJE = "mensaje";
    private static final String KEY_DATA = "data";

    /** Servicio que contiene la lógica de negocio para pasantías */
    @Autowired
    private PasantiaService pasantiaService;

    /** Servicio que maneja la seguridad y validación de permisos */
    @Autowired
    private SecurityService securityService;

    /**
     * Obtiene todas las pasantías del sistema (para administradores).
     */
    @GetMapping(produces = CONTENT_TYPE_JSON_UTF8)
    public ResponseEntity<List<PasantiaResponseDTO>> getAllPasantias() {
        List<PasantiaResponseDTO> pasantias = pasantiaService.obtenerTodasLasPasantias();
        return ResponseEntity.ok()
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON_UTF8)
                .body(pasantias);
    }

    /**
     * Obtiene todas las pasantías con estado PUBLICADA.
     * Endpoint público: las pasantías finalizadas NO aparecen aquí.
     */
    @GetMapping("/publicadas")
    public ResponseEntity<List<PasantiaResponseDTO>> getPasantiasPublicadas() {
        List<PasantiaResponseDTO> pasantias = pasantiaService.obtenerPasantiasPublicadas();
        return ResponseEntity.ok()
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON_UTF8)
                .body(pasantias);
    }

    /**
     * Obtiene los detalles completos de una pasantía por su ID.
     * Endpoint público.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getPasantiaById(@PathVariable Integer id) {
        try {
            // Obtener los detalles completos de la pasantía
            PasantiaDetalleDTO pasantia = pasantiaService.obtenerPasantiaPorId(id);
            return ResponseEntity.ok()
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON_UTF8)
                    .body(pasantia);
        } catch (IllegalArgumentException e) {
            // La pasantía no existe
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            // Error inesperado del servidor
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, "Error al obtener la pasantía: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Registra una nueva pasantía en el sistema.
     * La pasantía se crea directamente en estado PUBLICADA (sin aprobación).
     */
    @PostMapping("/registrar")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPRESA')")
    public ResponseEntity<Map<String, Object>> registrarPasantia(@Valid @RequestBody PasantiaRequestDTO request) {
        try {
            // Validar que el usuario autenticado tiene permiso para crear pasantía para esta empresa
            log.debug("Registrar pasantía. idEmpresa={}", request.getIdEmpresa());
            securityService.validarPermisoCrearPasantia(request.getIdEmpresa());
            
            // Crear la pasantía en la base de datos (se publica automáticamente)
            PasantiaResponseDTO pasantia = pasantiaService.crearPasantia(request);
            
            // Construir respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put(KEY_CODIGO, 0);
            response.put(KEY_MENSAJE, "Pasantía publicada exitosamente");
            response.put(KEY_DATA, pasantia);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON_UTF8)
                    .body(response);
        } catch (SecurityException | AccessDeniedException e) {
            // Usuario no tiene permisos para realizar esta acción
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (IllegalArgumentException e) {
            // Datos inválidos (empresa no existe, carrera no existe, fechas inválidas, etc.)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // Error inesperado del servidor
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, "Error al registrar la pasantía: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Finaliza una pasantía cambiando su estado a FINALIZADA.
     * 
     * <p>Accesible para:
     * <ul>
     *   <li>ADMINISTRADOR: puede finalizar cualquier pasantía</li>
     *   <li>EMPRESA: solo puede finalizar sus propias pasantías</li>
     * </ul>
     * 
     * <p>Una vez finalizada, la pasantía ya no es visible para los estudiantes
     * y no acepta nuevas postulaciones.
     */
    @PutMapping("/{id}/finalizar")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPRESA')")
    public ResponseEntity<Map<String, Object>> finalizarPasantia(@PathVariable Integer id) {
        try {
            // Validar permisos: admin puede finalizar cualquiera, empresa solo las suyas
            if (!securityService.esAdministrador()) {
                securityService.validarPermisoModificarPasantia(id);
            }
            
            // Crear DTO para actualizar estado a FINALIZADA
            ActualizarEstadoPasantiaDTO estadoDTO = new ActualizarEstadoPasantiaDTO();
            estadoDTO.setEstado(EstadoPasantia.FINALIZADA);
            
            // Actualizar el estado de la pasantía en la base de datos
            PasantiaResponseDTO pasantia = pasantiaService.actualizarEstado(id, estadoDTO);
            
            // Construir respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put(KEY_CODIGO, 0);
            response.put(KEY_MENSAJE, "Pasantía finalizada exitosamente");
            response.put(KEY_DATA, pasantia);
            
            return ResponseEntity.ok()
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON_UTF8)
                    .body(response);
        } catch (SecurityException | AccessDeniedException e) {
            // Usuario no tiene permisos
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (IllegalArgumentException e) {
            // La pasantía no existe
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalStateException e) {
            // La pasantía no está en un estado válido para ser finalizada
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // Error inesperado del servidor
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, "Error al finalizar la pasantía: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/misPasantias")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('EMPRESA')")
    public ResponseEntity<Map<String, Object>> obtenerMisPasantias() {
        try {
            List<PasantiaResponseDTO> pasantias = pasantiaService.obtenerMisPasantias();

            Map<String, Object> response = new HashMap<>();
            response.put(KEY_CODIGO, 0);
            response.put(KEY_MENSAJE, "Pasantías encontradas");
            response.put(KEY_DATA, pasantias);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, "Error al obtener pasantías: " + e.getMessage());
            errorResponse.put(KEY_DATA, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
