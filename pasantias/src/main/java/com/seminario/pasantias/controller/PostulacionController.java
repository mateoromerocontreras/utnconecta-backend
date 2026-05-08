package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.request.PostulacionRequestDTO;
import com.seminario.pasantias.dto.request.ActualizarEstadoPostulacionDTO;
import com.seminario.pasantias.dto.response.PostulacionResponseDTO;
import com.seminario.pasantias.service.PostulacionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/postulaciones")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"},
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
             allowedHeaders = "*")
public class PostulacionController {

    private static final Logger log = LoggerFactory.getLogger(PostulacionController.class);

    private static final String KEY_CODIGO = "codigo";
    private static final String KEY_MENSAJE = "mensaje";
    private static final String KEY_DATA = "data";
    private static final String SEP = "========================================";

    @Autowired
    private PostulacionService postulacionService;


    @PostMapping("/registrarPostulacion")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> registrarPostulacion(@Valid @RequestBody PostulacionRequestDTO request) {
        try {
            log.debug("{}\nPOSTULACION - Iniciando registro. estudianteId={}, pasantiaId={}, fecha={}, estado={}\n{}",
                    SEP,
                    request.getIdEstudiante(),
                    request.getIdPasantia(),
                    request.getFechaPostulacion(),
                    request.getEstado(),
                    SEP);
            
            PostulacionResponseDTO postulacion = postulacionService.crearPostulacion(request);

            log.debug("{}\nPOSTULACION - Registro exitoso. id={}, estudiante={} {}, pasantia={}, estado={}, fecha={}\n{}",
                    SEP,
                    postulacion.getIdPostulacion(),
                    postulacion.getNombreEstudiante(),
                    postulacion.getApellidoEstudiante(),
                    postulacion.getTituloPasantia(),
                    postulacion.getEstado(),
                    postulacion.getFechaPostulacion(),
                    SEP);

            Map<String, Object> response = new HashMap<>();
            response.put(KEY_CODIGO, 0);
            response.put(KEY_MENSAJE, "Postulación registrada exitosamente");
            response.put(KEY_DATA, postulacion);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.warn("{}\nPOSTULACION - Error al registrar. estudianteId={}, pasantiaId={}, error={}\n{}",
                    SEP,
                    request.getIdEstudiante(),
                    request.getIdPasantia(),
                    e.getMessage(),
                    SEP);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}/estado")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('EMPRESA')")
    public ResponseEntity<Map<String, Object>> actualizarEstadoYFinalizarCiclo(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarEstadoPostulacionDTO request
    ) {
        try {
            PostulacionResponseDTO postulacion = postulacionService.cubrirPostulacionYFinalizarPasantia(id, request);

            Map<String, Object> response = new HashMap<>();
            response.put(KEY_CODIGO, 0);
            response.put(KEY_MENSAJE, "Ciclo finalizado: postulación cubierta y pasantía finalizada");
            response.put(KEY_DATA, postulacion);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());
            errorResponse.put(KEY_DATA, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/consultarPostulaciones")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> consultarPostulaciones() {
        try {
            log.debug("Consultar postulaciones");
            List<PostulacionResponseDTO> postulaciones = postulacionService.consultarPostulaciones();

            Map<String, Object> response = new HashMap<>();
            response.put(KEY_CODIGO, 0);
            response.put(KEY_MENSAJE, "Postulaciones encontradas");
            response.put(KEY_DATA, postulaciones);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/misPostulaciones")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> misPostulaciones() {
        List<PostulacionResponseDTO> data = postulacionService.consultarMisPostulaciones();

        Map<String, Object> response = new HashMap<>();
        response.put(KEY_CODIGO, 0);
        response.put(KEY_DATA, data);
        response.put(KEY_MENSAJE, "Postulaciones encontradas");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/porPasantia/{pasantiaId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> obtenerPostulacionPorPasantia(@PathVariable Integer pasantiaId) {
        try {
            java.util.Optional<PostulacionResponseDTO> postulacionOpt = postulacionService.obtenerPostulacionPorPasantia(pasantiaId);

            Map<String, Object> response = new HashMap<>();
            if (postulacionOpt.isPresent()) {
                response.put(KEY_CODIGO, 0);
                response.put(KEY_MENSAJE, "Postulación encontrada");
                response.put(KEY_DATA, postulacionOpt.get());
                return ResponseEntity.ok(response);
            } else {
                response.put(KEY_CODIGO, 0);
                response.put(KEY_MENSAJE, "No se encontró postulación para esta pasantía");
                response.put(KEY_DATA, null);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());
            errorResponse.put(KEY_DATA, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/pasantia/{pasantiaId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE', 'EMPRESA')")
    public ResponseEntity<Map<String, Object>> obtenerTodasPostulacionesPorPasantia(@PathVariable Integer pasantiaId) {
        try {
            List<PostulacionResponseDTO> postulaciones = postulacionService.obtenerTodasPostulacionesPorPasantia(pasantiaId);

            Map<String, Object> response = new HashMap<>();
            response.put(KEY_CODIGO, 0);
            response.put(KEY_MENSAJE, "Postulaciones encontradas: " + postulaciones.size());
            response.put(KEY_DATA, postulaciones);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, "Error al obtener postulaciones: " + e.getMessage());
            errorResponse.put(KEY_DATA, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> obtenerPostulacionPorId(@PathVariable Integer id) {
        try {
            com.seminario.pasantias.dto.response.PostulacionDetalleDTO postulacion = postulacionService.obtenerPostulacionPorId(id);

            Map<String, Object> response = new HashMap<>();
            response.put(KEY_CODIGO, 0);
            response.put(KEY_MENSAJE, "Postulación encontrada");
            response.put(KEY_DATA, postulacion);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, e.getMessage());
            errorResponse.put(KEY_DATA, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, "Error al obtener la postulación: " + e.getMessage());
            errorResponse.put(KEY_DATA, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/postulacionesMiEmpresa")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('EMPRESA')")
    public ResponseEntity<Map<String, Object>> obtenerPostulacionesMiEmpresa() {
        try {
            List<PostulacionResponseDTO> postulaciones = postulacionService.obtenerPostulacionesMiEmpresa();

            Map<String, Object> response = new HashMap<>();
            response.put(KEY_CODIGO, 0);
            response.put(KEY_MENSAJE, "Postulaciones encontradas");
            response.put(KEY_DATA, postulaciones);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(KEY_CODIGO, -1);
            errorResponse.put(KEY_MENSAJE, "Error al obtener postulaciones: " + e.getMessage());
            errorResponse.put(KEY_DATA, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
