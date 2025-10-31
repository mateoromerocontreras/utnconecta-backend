package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.request.*;
import com.seminario.pasantias.dto.response.*;
import com.seminario.pasantias.entity.EstadoPasantia;
import com.seminario.pasantias.security.SecurityService;
import com.seminario.pasantias.service.PasantiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de Pasantías
 * 
 * Endpoints disponibles:
 * - POST /api/pasantias/crear - Crear nueva pasantía
 * - PUT /api/pasantias/{id}/actualizar - Actualizar pasantía existente
 * - PUT /api/pasantias/{id}/estado - Cambiar estado
 * - GET /api/pasantias/{id} - Obtener detalle completo
 * - GET /api/pasantias/buscar - Buscar con filtros
 * - GET /api/pasantias/empresa/{empresaId} - Por empresa
 * - GET /api/pasantias/carrera/{carreraId} - Por carrera
 * - GET /api/pasantias/publicadas - Solo publicadas
 * - DELETE /api/pasantias/{id} - Eliminar (soft delete)
 */
@RestController
@RequestMapping("/api/pasantias")
@CrossOrigin(origins = "*")
public class PasantiaController {

    private final PasantiaService pasantiaService;
    private final SecurityService securityService;

    @Autowired
    public PasantiaController(PasantiaService pasantiaService, SecurityService securityService) {
        this.pasantiaService = pasantiaService;
        this.securityService = securityService;
    }

    /**
     * Crear una nueva pasantía
     * 
     * POST /api/pasantias/crear
     * 
     * SEGURIDAD:
     * - Requiere autenticación con JWT
     * - Solo usuarios con rol EMPRESA o ADMINISTRADOR
     * - Usuario EMPRESA solo puede crear para su propia empresa
     * - Usuario ADMINISTRADOR puede crear para cualquier empresa
     * 
     * IMPORTANTE: El estado inicial siempre será PENDIENTE_DE_APROBACION
     * independientemente de lo que envíe el usuario en el request.
     * Esto es una regla de negocio que se aplica automáticamente en el Service.
     * 
     * @param request DTO con datos de la pasantía
     * @return PasantiaResponseDTO con la pasantía creada
     * 
     * Ejemplo Request:
     * {
     *   "titulo": "Desarrollador Backend Java",
     *   "puestoACubrir": "Desarrollador Junior",
     *   "ciudad": "Santa Fe",
     *   "modalidad": "Híbrida",
     *   "asignacionEstimulo": 50000.0,
     *   "cantidadDePasantes": 2,
     *   "fechaPublicacion": "2025-11-01",
     *   "fechaCaducidad": "2026-02-01",
     *   "idEmpresa": 1,
     *   "idsCarreras": [1, 2],
     *   "emailContacto": "rrhh@empresa.com"
     * }
     * 
     * Ejemplo Response 201:
     * {
     *   "idPasantia": 1,
     *   "titulo": "Desarrollador Backend Java",
     *   "estado": "PENDIENTE_DE_APROBACION",
     *   ...
     * }
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('EMPRESA', 'ADMINISTRADOR')")
    public ResponseEntity<?> crearPasantia(@Valid @RequestBody PasantiaRequestDTO request) {
        try {
            // 1. Validar permisos: el usuario debe poder crear pasantías para esta empresa
            securityService.validarPermisoCrearPasantia(request.getIdEmpresa());
            
            // 2. Crear la pasantía (Service forzará estado PENDIENTE_DE_APROBACION)
            PasantiaResponseDTO pasantia = pasantiaService.crearPasantia(request);
            
            // 3. Respuesta exitosa con código 201 Created
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Pasantía creada exitosamente. Estado inicial: PENDIENTE_DE_APROBACION");
            response.put("data", pasantia);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (SecurityException e) {
            // Error de permisos
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -3);
            errorResponse.put("mensaje", e.getMessage());
            errorResponse.put("tipo", "PERMISO_DENEGADO");
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            
        } catch (IllegalArgumentException e) {
            // Error de validación (empresa no existe, carrera no existe, etc.)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            errorResponse.put("tipo", "VALIDACION");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (IllegalStateException e) {
            // Error de regla de negocio
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -2);
            errorResponse.put("mensaje", e.getMessage());
            errorResponse.put("tipo", "REGLA_NEGOCIO");
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            
        } catch (Exception e) {
            // Error inesperado
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -99);
            errorResponse.put("mensaje", "Error interno del servidor: " + e.getMessage());
            errorResponse.put("tipo", "ERROR_INTERNO");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualizar una pasantía existente
     * 
     * PUT /api/pasantias/{id}/actualizar
     * 
     * SEGURIDAD:
     * - Requiere autenticación con JWT
     * - Solo usuarios con rol EMPRESA o ADMINISTRADOR
     * - Usuario EMPRESA solo puede modificar pasantías de su empresa
     * - Usuario ADMINISTRADOR puede modificar cualquier pasantía
     * 
     * Solo se pueden modificar pasantías que NO estén en estado
     * FINALIZADA o DADA_DE_BAJA.
     */
    @PutMapping("/{id}/actualizar")
    @PreAuthorize("hasAnyRole('EMPRESA', 'ADMINISTRADOR')")
    public ResponseEntity<?> actualizarPasantia(
            @PathVariable Integer id,
            @Valid @RequestBody PasantiaRequestDTO request) {
        try {
            // 1. Validar permisos de modificación
            securityService.validarPermisoModificarPasantia(id);
            
            // 2. Actualizar pasantía
            PasantiaResponseDTO pasantia = pasantiaService.actualizarPasantia(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Pasantía actualizada exitosamente");
            response.put("data", pasantia);
            
            return ResponseEntity.ok(response);
            
        } catch (SecurityException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -3);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -2);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    /**
     * Cambiar el estado de una pasantía
     * 
     * PUT /api/pasantias/{id}/estado
     * 
     * SEGURIDAD:
     * - Usuario ADMINISTRADOR puede cambiar cualquier estado
     * - Usuario EMPRESA puede cambiar estados de sus pasantías (excepto aprobar/rechazar)
     * 
     * Transiciones válidas:
     * PENDIENTE_DE_APROBACION -> PUBLICADA | DADA_DE_BAJA (solo ADMINISTRADOR puede aprobar)
     * PUBLICADA -> FINALIZADA | DADA_DE_BAJA | EXPIRADA
     * 
     * Ejemplo Request:
     * {
     *   "estado": "PUBLICADA",
     *   "motivo": "Pasantía aprobada por el administrador"
     * }
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('EMPRESA', 'ADMINISTRADOR')")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarEstadoPasantiaDTO request) {
        try {
            // 1. Validar permisos de modificación
            securityService.validarPermisoModificarPasantia(id);
            
            // 2. Cambiar estado
            PasantiaResponseDTO pasantia = pasantiaService.actualizarEstado(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Estado actualizado a: " + pasantia.getEstado());
            response.put("data", pasantia);
            
            return ResponseEntity.ok(response);
            
        } catch (SecurityException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -3);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -2);
            errorResponse.put("mensaje", e.getMessage());
            errorResponse.put("estadoActual", request.getEstado());
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    /**
     * Obtener detalle completo de una pasantía
     * 
     * GET /api/pasantias/{id}
     * 
     * Incluye: datos completos, empresa, carreras, postulaciones y estadísticas
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPasantia(@PathVariable Integer id) {
        try {
            PasantiaDetalleDTO pasantia = pasantiaService.obtenerPasantiaPorId(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Pasantía encontrada");
            response.put("data", pasantia);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Buscar pasantías con filtros y paginación
     * 
     * GET /api/pasantias/buscar
     * 
     * Query params opcionales:
     * - titulo, ciudad, modalidad, estado
     * - empresaId, carreraIds
     * - asignacionMinima, asignacionMaxima
     * - fechaPublicacionDesde, fechaPublicacionHasta
     * - fechaCaducidadDesde, fechaCaducidadHasta
     * - pagina, tamanio, ordenarPor, ordenDireccion
     * 
     * Ejemplo: GET /api/pasantias/buscar?estado=PUBLICADA&ciudad=Santa%20Fe&pagina=0&tamanio=10
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPasantias(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String modalidad,
            @RequestParam(required = false) EstadoPasantia estado,
            @RequestParam(required = false) Integer empresaId,
            @RequestParam(required = false) List<Integer> carreraIds,
            @RequestParam(required = false) Float asignacionMinima,
            @RequestParam(required = false) Float asignacionMaxima,
            @RequestParam(required = false) String fechaPublicacionDesde,
            @RequestParam(required = false) String fechaPublicacionHasta,
            @RequestParam(required = false) String fechaCaducidadDesde,
            @RequestParam(required = false) String fechaCaducidadHasta,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "20") Integer tamanio,
            @RequestParam(defaultValue = "fechaPublicacion") String ordenarPor,
            @RequestParam(defaultValue = "DESC") String ordenDireccion) {
        
        try {
            // Construir filtro
            PasantiaFiltroDTO filtro = new PasantiaFiltroDTO();
            filtro.setBusqueda(titulo); // "busqueda" busca en título o puesto
            filtro.setCiudad(ciudad);
            filtro.setModalidad(modalidad);
            filtro.setEstado(estado);
            filtro.setIdEmpresa(empresaId);
            filtro.setIdsCarreras(carreraIds);
            filtro.setAsignacionMinima(asignacionMinima);
            filtro.setAsignacionMaxima(asignacionMaxima);
            filtro.setPagina(pagina);
            filtro.setTamanio(tamanio);
            filtro.setOrdenarPor(ordenarPor);
            filtro.setDireccion(ordenDireccion);
            
            // TODO: Parsear fechas si es necesario
            
            PaginaDTO<PasantiaResponseDTO> resultado = pasantiaService.buscarPasantias(filtro);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Búsqueda exitosa");
            response.put("data", resultado);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -99);
            errorResponse.put("mensaje", "Error en la búsqueda: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener pasantías de una empresa
     * 
     * GET /api/pasantias/empresa/{empresaId}
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<?> obtenerPasantiasPorEmpresa(@PathVariable Integer empresaId) {
        try {
            List<PasantiaResponseDTO> pasantias = pasantiaService.obtenerPasantiasPorEmpresa(empresaId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Se encontraron " + pasantias.size() + " pasantías");
            response.put("data", pasantias);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -99);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener pasantías por carrera
     * 
     * GET /api/pasantias/carrera/{carreraId}
     */
    @GetMapping("/carrera/{carreraId}")
    public ResponseEntity<?> obtenerPasantiasPorCarrera(@PathVariable Integer carreraId) {
        try {
            List<PasantiaResponseDTO> pasantias = pasantiaService.obtenerPasantiasPorCarrera(carreraId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Se encontraron " + pasantias.size() + " pasantías");
            response.put("data", pasantias);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -99);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Obtener pasantías publicadas (disponibles para postular)
     * 
     * GET /api/pasantias/publicadas
     */
    @GetMapping("/publicadas")
    public ResponseEntity<?> obtenerPasantiasPublicadas() {
        try {
            List<PasantiaResponseDTO> pasantias = pasantiaService.obtenerPasantiasPublicadas();
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Se encontraron " + pasantias.size() + " pasantías publicadas");
            response.put("data", pasantias);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -99);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Eliminar pasantía (soft delete)
     * 
     * DELETE /api/pasantias/{id}
     * 
     * SEGURIDAD:
     * - Solo usuarios con rol EMPRESA o ADMINISTRADOR
     * - Usuario EMPRESA solo puede eliminar pasantías de su empresa
     * - Usuario ADMINISTRADOR puede eliminar cualquier pasantía
     * 
     * La pasantía pasa a estado DADA_DE_BAJA.
     * No se puede eliminar si tiene postulaciones activas.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPRESA', 'ADMINISTRADOR')")
    public ResponseEntity<?> eliminarPasantia(@PathVariable Integer id) {
        try {
            // 1. Validar permisos
            securityService.validarPermisoModificarPasantia(id);
            
            // 2. Eliminar
            pasantiaService.eliminarPasantia(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Pasantía eliminada exitosamente (estado: DADA_DE_BAJA)");
            
            return ResponseEntity.ok(response);
            
        } catch (SecurityException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -3);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -2);
            errorResponse.put("mensaje", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
}
