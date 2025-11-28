package com.seminario.pasantias.util;

import com.seminario.pasantias.dto.request.PostulacionRequestDTO;
import com.seminario.pasantias.dto.response.*;
import com.seminario.pasantias.dto.response.PostulacionDetalleDTO.*;
import com.seminario.pasantias.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Utilidad para conversión entre DTOs y Entities de Postulacion
 */
@Component
public class PostulacionMapperUtil {

    /**
     * Convierte PostulacionRequestDTO a Entity Postulacion
     */
    public Postulacion requestDtoToEntity(PostulacionRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Postulacion postulacion = new Postulacion();
        
        if (dto.getFechaPostulacion() != null) {
            postulacion.setFechaPostulacion(dto.getFechaPostulacion());
        } else {
            postulacion.setFechaPostulacion(LocalDate.now());
        }
        
        postulacion.setFechaInicioContrato(dto.getFechaInicioContrato());
        postulacion.setDuracionMeses(dto.getDuracionMeses());
        
        if (dto.getEstado() != null) {
            postulacion.setEstado(dto.getEstado());
        } else {
            postulacion.setEstado(EstadoPostulacion.BORRADOR);
        }

        // Pasantia y Estudiante se setean por separado en el servicio
        return postulacion;
    }

    /**
     * Actualiza una entity Postulacion desde un RequestDTO
     */
    public void updateEntityFromRequestDto(PostulacionRequestDTO dto, Postulacion postulacion) {
        if (dto == null || postulacion == null) {
            return;
        }

        if (dto.getFechaInicioContrato() != null) {
            postulacion.setFechaInicioContrato(dto.getFechaInicioContrato());
        }
        if (dto.getDuracionMeses() != null) {
            postulacion.setDuracionMeses(dto.getDuracionMeses());
        }
        if (dto.getEstado() != null) {
            postulacion.setEstado(dto.getEstado());
        }
    }

    /**
     * Convierte Entity Postulacion a PostulacionResponseDTO
     */
    public PostulacionResponseDTO entityToResponseDto(Postulacion postulacion) {
        if (postulacion == null) {
            return null;
        }

        PostulacionResponseDTO dto = new PostulacionResponseDTO();
        dto.setIdPostulacion(postulacion.getIdPostulacion());
        dto.setFechaPostulacion(postulacion.getFechaPostulacion());
        dto.setFechaInicioContrato(postulacion.getFechaInicioContrato());
        dto.setDuracionMeses(postulacion.getDuracionMeses());
        dto.setEstado(postulacion.getEstado());

        // Pasantia (desnormalizado)
        if (postulacion.getPasantia() != null) {
            Pasantia pasantia = postulacion.getPasantia();
            dto.setIdPasantia(pasantia.getIdPasantia());
            dto.setTituloPasantia(pasantia.getTitulo());
            dto.setModalidad(pasantia.getModalidad());
            
            if (pasantia.getEmpresa() != null) {
                dto.setNombreEmpresa(pasantia.getEmpresa().getNombre());
            }
        }

        // Estudiante (desnormalizado)
        if (postulacion.getEstudiante() != null) {
            Estudiante estudiante = postulacion.getEstudiante();
            dto.setIdEstudiante(estudiante.getIdEstudiante());
            dto.setNombreEstudiante(estudiante.getNombre() + " " + estudiante.getApellido());
        }

        // Campos calculados
        dto.setEsEditable(
                postulacion.getEstado() == EstadoPostulacion.BORRADOR ||
                postulacion.getEstado() == EstadoPostulacion.PENDIENTE_APROBACION
        );

        return dto;
    }

    /**
     * Convierte Entity Postulacion a PostulacionDetalleDTO (con toda la información)
     */
    public PostulacionDetalleDTO entityToDetalleDto(Postulacion postulacion) {
        if (postulacion == null) {
            return null;
        }

        PostulacionDetalleDTO dto = new PostulacionDetalleDTO();
        dto.setIdPostulacion(postulacion.getIdPostulacion());
        dto.setFechaPostulacion(postulacion.getFechaPostulacion());
        dto.setFechaInicioContrato(postulacion.getFechaInicioContrato());
        dto.setDuracionMeses(postulacion.getDuracionMeses());
        dto.setEstado(postulacion.getEstado());

        // Pasantia (con detalle)
        if (postulacion.getPasantia() != null) {
            Pasantia pasantia = postulacion.getPasantia();
            PasantiaSimpleDTO pasantiaDto = new PasantiaSimpleDTO();
            pasantiaDto.setIdPasantia(pasantia.getIdPasantia());
            pasantiaDto.setTitulo(pasantia.getTitulo());
            pasantiaDto.setPuestoACubrir(pasantia.getPuestoACubrir());
            pasantiaDto.setCiudad(pasantia.getCiudad());
            pasantiaDto.setModalidad(pasantia.getModalidad());
            pasantiaDto.setAsignacionEstimulo(pasantia.getAsignacionEstimulo());
            pasantiaDto.setEstadoPasantia(pasantia.getEstado().name());
            pasantiaDto.setEmailContacto(pasantia.getEmailContacto());
            
            if (pasantia.getEmpresa() != null) {
                pasantiaDto.setNombreEmpresa(pasantia.getEmpresa().getNombre());
            }
            
            dto.setPasantia(pasantiaDto);
        }

        // Estudiante (con detalle)
        if (postulacion.getEstudiante() != null) {
            Estudiante estudiante = postulacion.getEstudiante();
            EstudianteSimpleDTO estudianteDto = new EstudianteSimpleDTO();
            estudianteDto.setIdEstudiante(estudiante.getIdEstudiante());
            estudianteDto.setNombre(estudiante.getNombre());
            estudianteDto.setApellido(estudiante.getApellido());
            estudianteDto.setEmail(estudiante.getEmail());
            // telefono, nombreCarrera y codigoCarrera se pueden agregar si existen en Estudiante
            dto.setEstudiante(estudianteDto);
        }

        // Campos calculados
        dto.setEsEditable(
                postulacion.getEstado() == EstadoPostulacion.BORRADOR ||
                postulacion.getEstado() == EstadoPostulacion.PENDIENTE_APROBACION
        );

        return dto;
    }
}
