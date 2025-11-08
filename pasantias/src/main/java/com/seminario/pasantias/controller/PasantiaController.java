package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.request.PasantiaRequestDTO;
import com.seminario.pasantias.dto.response.PasantiaResponseDTO;
import com.seminario.pasantias.service.PasantiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pasantias")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"},
             allowedHeaders = "*")
public class PasantiaController {

    @Autowired
    private PasantiaService pasantiaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarPasantia(@Valid @RequestBody PasantiaRequestDTO request) {
        try {
            PasantiaResponseDTO pasantia = pasantiaService.crearPasantia(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("codigo", 0);
            response.put("mensaje", "Pasantía registrada exitosamente");
            response.put("data", pasantia);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
}

