package com.seminario.pasantias.dto.response;

import com.seminario.pasantias.entity.EstadoPostulacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta detallada para Postulación.
 * Incluye información completa de Pasantía y Estudiante.
 * Ideal para vista de detalle de una postulación específica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostulacionDetalleDTO {

    /**
     * ID único de la postulación
     */
    private Integer idPostulacion;

    /**
     * Fecha de postulación
     */
    private LocalDate fechaPostulacion;

    /**
     * Fecha de inicio del contrato
     */
    private LocalDate fechaInicioContrato;

    /**
     * Duración del contrato en meses
     */
    private Integer duracionMeses;

    /**
     * Estado actual de la postulación
     */
    private EstadoPostulacion estado;

    /**
     * Información de la pasantía
     */
    private PasantiaSimpleDTO pasantia;

    /**
     * Información del estudiante
     */
    private EstudianteSimpleDTO estudiante;

    /**
     * Indica si puede ser editada
     */
    private Boolean esEditable;

    /**
     * Fecha de creación
     */
    private LocalDate fechaCreacion;

    /**
     * Fecha de última actualización
     */
    private LocalDate fechaActualizacion;

    /**
     * Historial de cambios de estado (opcional)
     */
    private String observaciones;

    /**
     * DTO simple para representar una Pasantía
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PasantiaSimpleDTO {
        private Integer idPasantia;
        private String titulo;
        private String puestoACubrir;
        private String ciudad;
        private String modalidad;
        private Float asignacionEstimulo;
        private String estadoPasantia;
        private String nombreEmpresa;
        private String emailContacto;
    }

    /**
     * DTO simple para representar un Estudiante
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstudianteSimpleDTO {
        private Integer idEstudiante;
        private String nombre;
        private String apellido;
        private String email;
        private String telefono;
        private String nombreCarrera;
        private String codigoCarrera;
    }
}
