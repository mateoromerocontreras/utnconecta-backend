package com.seminario.pasantias.controller;

import com.seminario.pasantias.entity.Carrera;
import com.seminario.pasantias.dto.CarreraRequest;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.CarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carreras")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
             allowedHeaders = "*")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;

    @PostMapping("/registrarCarrera")
    public GenericResponse registrarCarrera(@RequestBody CarreraRequest request) {
        GenericResponse response = new GenericResponse();
        if (request.getNombre() == null || request.getNombre().isEmpty()) {
            response.setCode(-1);
            response.setMessage("El nombre es obligatorio");
            return response;
        }
        try {
            Carrera carrera = new Carrera(null, request.getNombre());
            carreraService.createCarrera(carrera);
            response.setCode(0);
            response.setMessage(null);
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GetMapping("/consultarCarrera")
    public List<String> consultarCarrera(@RequestParam(required = false) String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            List<Carrera> carreras = carreraService.getAllCarreras();
            return carreras.stream()
                    .map(Carrera::getNombre)
                    .collect(Collectors.toList());
        } else {
            Optional<Carrera> carrera = carreraService.getCarreraByNombre(nombre);
            return carrera.map(c -> List.of(c.getNombre()))
                    .orElse(List.of());
        }
    }

    @PostMapping("/updateCarrera")
    public GenericResponse updateCarrera(@RequestBody CarreraRequest request) {
        System.out.println("=== DEBUG UPDATE CARRERA ===");
        System.out.println("Request ID: " + request.getId());
        System.out.println("Request Nombre: " + request.getNombre());
        
        GenericResponse response = new GenericResponse();
        if (request.getId() == null || request.getNombre() == null || request.getNombre().isEmpty()) {
            response.setCode(-1);
            response.setMessage("El id y el nombre son obligatorios");
            return response;
        }
        try {
            Carrera carrera = new Carrera(request.getId(), request.getNombre());
            System.out.println("Carrera a actualizar: ID=" + carrera.getId() + ", Nombre=" + carrera.getNombre());
            carreraService.updateCarrera(carrera);
            System.out.println("Carrera actualizada exitosamente");
            response.setCode(0);
            response.setMessage(null);
        } catch (Exception e) {
            System.err.println("Error al actualizar carrera: " + e.getMessage());
            e.printStackTrace();
            response.setCode(-1);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PostMapping("/deleteCarrera")
    public GenericResponse deleteCarrera(@RequestBody CarreraRequest request) {
        GenericResponse response = new GenericResponse();
        if (request.getNombre() == null || request.getNombre().isEmpty()) {
            response.setCode(-1);
            response.setMessage("El nombre es obligatorio");
            return response;
        }
        try {
            carreraService.deleteCarreraByNombre(request.getNombre());
            response.setCode(0);
            response.setMessage(null);
        } catch (Exception e) {
            response.setCode(-1);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GetMapping("/listarCarreras")
    public List<Carrera> listarCarreras() {
        return carreraService.getAllCarreras();
    }
}