package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.request.PostulacionRequestDTO;
import com.seminario.pasantias.dto.response.PostulacionResponseDTO;
import com.seminario.pasantias.service.PostulacionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private PostulacionService postulacionService;


    @PostMapping("/registrarPostulacion")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<?> registrarPostulacion(@Valid @RequestBody PostulacionRequestDTO request) {
        try {
            PostulacionResponseDTO postulacion = postulacionService.crearPostulacion(request);

            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Postulación registrada exitosamente");
            response.put("data", postulacion);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/consultarPostulaciones")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<?> consultarPostulaciones() {
        try {
            System.out.println(":::::: AGUSTIN ENTRA A CONSULTAR POSTULACION ::::::");
            List<PostulacionResponseDTO> postulaciones = postulacionService.consultarPostulaciones();

            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Postulaciones encontradas");
            response.put("data", postulaciones);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("codigo", -1);
            errorResponse.put("mensaje", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/misPostulaciones")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESTUDIANTE')")
    public ResponseEntity<Map<String, Object>> misPostulaciones() {
        List<PostulacionResponseDTO> data = postulacionService.consultarMisPostulaciones();

        Map<String, Object> response = new HashMap<>();
        response.put("codigo", 0);
        response.put("data", data);
        response.put("mensaje", "Postulaciones encontradas");

        return ResponseEntity.ok(response);
    }
    

}

