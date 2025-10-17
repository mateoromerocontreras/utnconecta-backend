package com.seminario.pasantias.service;

import com.seminario.pasantias.dto.*;
import com.seminario.pasantias.entity.*;
import com.seminario.pasantias.persistence.PostulacionMapper;
import com.seminario.pasantias.persistence.EstudianteMapper;
import com.seminario.pasantias.response.GenericResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostulacionService {

    private final PostulacionMapper postulacionMapper;
    private final EstudianteMapper estudianteMapper;

    @Autowired
    public PostulacionService(PostulacionMapper postulacionMapper, EstudianteMapper estudianteMapper) {
        this.postulacionMapper = postulacionMapper;
        this.estudianteMapper = estudianteMapper;
    }

    public GenericResponse registrarPostulacion(PostulacionRequest request) {
        GenericResponse response = new GenericResponse();

        if (request.getEstudianteId() == null) //|| request.getPasantiaId() == null) falta agregar lo de pasantia
            {
            response.setCode(-1);
            response.setMessage("Campos obligatorios: estudianteId y pasantiaId");
            return response;
        }

        // int count = postulacionMapper.countByEstudianteAndPasantia(request.getEstudianteId(), request.getPasantiaId());
        // if (count > 0) {
        //     response.setCode(-1);
        //     response.setMessage("El estudiante ya se postuló a esta pasantía");
        //     return response;
        // } falta agregar lo de pasantia

        Estudiante estudiante = estudianteMapper.findById(request.getEstudianteId()).orElse(null);
        if (estudiante == null) {
            response.setCode(-1);
            response.setMessage("El estudiante no existe");
            return response;
        }

        boolean existe = postulacionMapper.existsByEstudiante(request.getEstudianteId());
        if (existe) {
            response.setCode(-1);
            response.setMessage("Ya existe una postulación de este estudiante");
            return response;
        }

        Postulacion postulacion = new Postulacion();
        postulacion.setIdEstudiante(request.getEstudianteId());
        postulacion.setFecha(LocalDateTime.now());
        postulacion.setEstado("Pendiente");

        postulacionMapper.insert(postulacion);

        response.setCode(0);
        response.setMessage("Postulación registrada correctamente");
        return response;
    }

    public List<Postulacion> consultarPostulaciones() {
        return postulacionMapper.findAll();
    }


    public GenericResponse modificarPostulacion(PostulacionUpdateRequest request) {
        GenericResponse response = new GenericResponse();

        if (request.getId() == null || request.getEstado() == null) {
            response.setCode(-1);
            response.setMessage("El ID y el estado son obligatorios");
            return response;
        }

        Postulacion postulacion = postulacionMapper.findById(request.getId());
        if (postulacion == null) {
            response.setCode(-1);
            response.setMessage("No se encontró la postulación");
            return response;
        }

        if (!"Pendiente".equalsIgnoreCase(postulacion.getEstado())) {
            response.setCode(-1);
            response.setMessage("Solo se pueden modificar postulaciones en estado Pendiente");
            return response;
        }

        if (!request.getEstado().matches("Aceptada|Rechazada|Cancelada")) {
            response.setCode(-1);
            response.setMessage("Estado inválido");
            return response;
        }

        postulacion.setEstado(request.getEstado());
        postulacion.setObservaciones(request.getObservaciones());
        postulacion.setFechaActualizacion(LocalDateTime.now());

        postulacionMapper.update(postulacion);

        response.setCode(0);
        response.setMessage("Postulación actualizada correctamente");
        return response;
    }
}
