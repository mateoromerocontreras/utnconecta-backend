package com.seminario.pasantias.util;

import com.seminario.pasantias.dto.request.PasantiaRequestDTO;
import com.seminario.pasantias.dto.response.*;
import com.seminario.pasantias.dto.response.PasantiaDetalleDTO.*;
import com.seminario.pasantias.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para conversión entre DTOs y Entities de Pasantia
 */
@Component
public class PasantiaMapperUtil {

    /**
     * Convierte PasantiaRequestDTO a Entity Pasantia
     */
    public Pasantia requestDtoToEntity(PasantiaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Pasantia pasantia = new Pasantia();
        pasantia.setTitulo(dto.getTitulo());
        pasantia.setPuestoACubrir(dto.getPuestoACubrir());
        pasantia.setCiudad(dto.getCiudad());
        pasantia.setModalidad(dto.getModalidad());
        pasantia.setAsignacionEstimulo(dto.getAsignacionEstimulo());
        pasantia.setCantidadDePasantes(dto.getCantidadDePasantes());
        pasantia.setFechaPublicacion(dto.getFechaPublicacion());
        pasantia.setFechaCaducidad(dto.getFechaCaducidad());
        pasantia.setEmailContacto(dto.getEmailContacto());
        pasantia.setConocimientos(dto.getConocimientos());
        pasantia.setOtrosRequisitos(dto.getOtrosRequisitos());
        pasantia.setBeneficios(dto.getBeneficios());
        
        if (dto.getEstado() != null) {
            pasantia.setEstado(dto.getEstado());
        } else {
            pasantia.setEstado(EstadoPasantia.PUBLICADA);
        }

        // La empresa y carreras se setean por separado en el servicio
        return pasantia;
    }

    /**
     * Actualiza una entity Pasantia desde un RequestDTO
     */
    public void updateEntityFromRequestDto(PasantiaRequestDTO dto, Pasantia pasantia) {
        if (dto == null || pasantia == null) {
            return;
        }

        if (dto.getTitulo() != null) {
            pasantia.setTitulo(dto.getTitulo());
        }
        if (dto.getPuestoACubrir() != null) {
            pasantia.setPuestoACubrir(dto.getPuestoACubrir());
        }
        if (dto.getCiudad() != null) {
            pasantia.setCiudad(dto.getCiudad());
        }
        if (dto.getModalidad() != null) {
            pasantia.setModalidad(dto.getModalidad());
        }
        if (dto.getAsignacionEstimulo() != null) {
            pasantia.setAsignacionEstimulo(dto.getAsignacionEstimulo());
        }
        if (dto.getCantidadDePasantes() != null) {
            pasantia.setCantidadDePasantes(dto.getCantidadDePasantes());
        }
        if (dto.getFechaCaducidad() != null) {
            pasantia.setFechaCaducidad(dto.getFechaCaducidad());
        }
        if (dto.getEmailContacto() != null) {
            pasantia.setEmailContacto(dto.getEmailContacto());
        }
        if (dto.getConocimientos() != null) {
            pasantia.setConocimientos(dto.getConocimientos());
        }
        if (dto.getOtrosRequisitos() != null) {
            pasantia.setOtrosRequisitos(dto.getOtrosRequisitos());
        }
        if (dto.getBeneficios() != null) {
            pasantia.setBeneficios(dto.getBeneficios());
        }
    }

    /**
     * Convierte Entity Pasantia a PasantiaResponseDTO
     */
    public PasantiaResponseDTO entityToResponseDto(Pasantia pasantia) {
        if (pasantia == null) {
            return null;
        }

        PasantiaResponseDTO dto = new PasantiaResponseDTO();
        dto.setIdPasantia(pasantia.getIdPasantia());
        dto.setTitulo(pasantia.getTitulo());
        dto.setPuestoACubrir(pasantia.getPuestoACubrir());
        dto.setCiudad(pasantia.getCiudad());
        dto.setModalidad(pasantia.getModalidad());
        dto.setAsignacionEstimulo(pasantia.getAsignacionEstimulo());
        dto.setCantidadDePasantes(pasantia.getCantidadDePasantes());
        dto.setFechaPublicacion(pasantia.getFechaPublicacion());
        dto.setFechaCaducidad(pasantia.getFechaCaducidad());
        dto.setEstado(pasantia.getEstado());
        dto.setEmailContacto(pasantia.getEmailContacto());
        dto.setConocimientos(pasantia.getConocimientos());
        dto.setOtrosRequisitos(pasantia.getOtrosRequisitos());
        dto.setBeneficios(pasantia.getBeneficios());

        // Empresa
        if (pasantia.getEmpresa() != null) {
            dto.setIdEmpresa(pasantia.getEmpresa().getIdEmpresa());
            dto.setNombreEmpresa(pasantia.getEmpresa().getNombre());
        }

        // Campos calculados
        if (pasantia.getPostulaciones() != null) {
            dto.setCantidadPostulaciones(pasantia.getPostulaciones().size());
        } else {
            dto.setCantidadPostulaciones(0);
        }

        // Días restantes hasta caducidad
        if (pasantia.getFechaCaducidad() != null) {
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), pasantia.getFechaCaducidad());
            dto.setDiasRestantes(diasRestantes);
        }

        // Acepta postulaciones si está publicada
        dto.setAceptaPostulaciones(
                pasantia.getEstado() == EstadoPasantia.PUBLICADA
        );

        return dto;
    }

    /**
     * Convierte Entity Pasantia a PasantiaDetalleDTO (con toda la información)
     */
    public PasantiaDetalleDTO entityToDetalleDto(Pasantia pasantia) {
        if (pasantia == null) {
            return null;
        }

        PasantiaDetalleDTO dto = new PasantiaDetalleDTO();
        dto.setIdPasantia(pasantia.getIdPasantia());
        dto.setTitulo(pasantia.getTitulo());
        dto.setPuestoACubrir(pasantia.getPuestoACubrir());
        dto.setCiudad(pasantia.getCiudad());
        dto.setModalidad(pasantia.getModalidad());
        dto.setAsignacionEstimulo(pasantia.getAsignacionEstimulo());
        dto.setCantidadDePasantes(pasantia.getCantidadDePasantes());
        dto.setFechaPublicacion(pasantia.getFechaPublicacion());
        dto.setFechaCaducidad(pasantia.getFechaCaducidad());
        dto.setEstado(pasantia.getEstado());
        dto.setEmailContacto(pasantia.getEmailContacto());
        dto.setConocimientos(pasantia.getConocimientos());
        dto.setOtrosRequisitos(pasantia.getOtrosRequisitos());
        dto.setBeneficios(pasantia.getBeneficios());

        // Empresa (con detalle)
        if (pasantia.getEmpresa() != null) {
            EmpresaSimpleDTO empresaDto = new EmpresaSimpleDTO();
            empresaDto.setIdEmpresa(pasantia.getEmpresa().getIdEmpresa());
            empresaDto.setNombre(pasantia.getEmpresa().getNombre());
            empresaDto.setCuit(pasantia.getEmpresa().getCuit());
            empresaDto.setEmail(pasantia.getEmpresa().getEmail());
            dto.setEmpresa(empresaDto);
        }

        // Carreras (con detalle)
        if (pasantia.getCarreras() != null && !pasantia.getCarreras().isEmpty()) {
            List<CarreraSimpleDTO> carrerasDto = pasantia.getCarreras().stream()
                    .map(carrera -> {
                        CarreraSimpleDTO cDto = new CarreraSimpleDTO();
                        cDto.setIdCarrera(carrera.getId());
                        cDto.setNombre(carrera.getNombre());
                        cDto.setCodigo(carrera.getNombre()); // Ajustar si hay campo código
                        return cDto;
                    })
                    .toList();
            dto.setCarreras(carrerasDto);
        }

        // Postulaciones (con detalle)
        if (pasantia.getPostulaciones() != null && !pasantia.getPostulaciones().isEmpty()) {
            List<PostulacionSimpleDTO> postulacionesDto = pasantia.getPostulaciones().stream()
                    .map(postulacion -> {
                        PostulacionSimpleDTO pDto = new PostulacionSimpleDTO();
                        pDto.setIdPostulacion(postulacion.getIdPostulacion());
                        pDto.setFechaPostulacion(postulacion.getFechaPostulacion());
                        pDto.setEstadoPostulacion(postulacion.getEstado().name());
                        
                        if (postulacion.getEstudiante() != null) {
                            pDto.setIdEstudiante(postulacion.getEstudiante().getIdEstudiante());
                            pDto.setNombreEstudiante(
                                    postulacion.getEstudiante().getNombre() + " " + 
                                    postulacion.getEstudiante().getApellido()
                            );
                        }
                        
                        return pDto;
                    })
                    .toList();
            dto.setPostulaciones(postulacionesDto);
        } else {
            dto.setPostulaciones(new ArrayList<>());
        }

        // Estadísticas de postulaciones
        if (pasantia.getPostulaciones() != null) {
            EstadisticasPostulacionDTO stats = new EstadisticasPostulacionDTO();
            stats.setTotal(pasantia.getPostulaciones().size());
            stats.setBorradores((int) pasantia.getPostulaciones().stream()
                    .filter(p -> p.getEstado() == EstadoPostulacion.BORRADOR).count());
            stats.setPendientes((int) pasantia.getPostulaciones().stream()
                    .filter(p -> p.getEstado() == EstadoPostulacion.PENDIENTE_APROBACION).count());
            stats.setPublicadas((int) pasantia.getPostulaciones().stream()
                    .filter(p -> p.getEstado() == EstadoPostulacion.PUBLICADA).count());
            stats.setCubiertas((int) pasantia.getPostulaciones().stream()
                    .filter(p -> p.getEstado() == EstadoPostulacion.CUBIERTA).count());
            stats.setFinalizadas((int) pasantia.getPostulaciones().stream()
                    .filter(p -> p.getEstado() == EstadoPostulacion.FINALIZADA).count());
            dto.setEstadisticas(stats);
        }

        // Días restantes
        if (pasantia.getFechaCaducidad() != null) {
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), pasantia.getFechaCaducidad());
            dto.setDiasRestantes(diasRestantes);
        }

        // Acepta postulaciones si está publicada
        dto.setAceptaPostulaciones(
                pasantia.getEstado() == EstadoPasantia.PUBLICADA
        );

        return dto;
    }
}
