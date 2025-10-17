package com.seminario.pasantias.controller;

import com.seminario.pasantias.dto.*;
import com.seminario.pasantias.entity.Postulacion;
import com.seminario.pasantias.response.GenericResponse;
import com.seminario.pasantias.service.PostulacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/postulaciones")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"},
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
             allowedHeaders = "*")
public class PostulacionController {

    @Autowired
    private PostulacionService postulacionService;

    @PostMapping("/registrarPostulacion")
    public GenericResponse registrarPostulacion(@RequestBody PostulacionRequest request) {
        return postulacionService.registrarPostulacion(request);
    }

    @GetMapping("/consultarPostulaciones")
    public List<Postulacion> consultarPostulaciones() {
        return postulacionService.consultarPostulaciones();
    }

    @PutMapping("/modificarPostulacion")
    public GenericResponse modificarPostulacion(@RequestBody PostulacionUpdateRequest request) {
        return postulacionService.modificarPostulacion(request);
    }
}
