package com.seminario.pasantias.dto.response;

import com.seminario.pasantias.entity.EstadoPostulacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta básica para Postulación.
 * Contiene información esencial sin relaciones anidadas completas.
 * Ideal para listados y búsquedas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostulacionResponseDTO {

    /**
     * ID único de la postulación
     */
    private Integer idPostulacion;

    /**
     * Fecha de postulación
     */
    private LocalDate fechaPostulacion;

    /**
     * Fecha de inicio del contrato (si está definida)
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
     * ID de la pasantía
     */
    private Integer idPasantia;

    /**
     * Título de la pasantía (desnormalizado)
     */
    private String tituloPasantia;

    /**
     * ID del estudiante
     */
    private Integer idEstudiante;

    /**
     * Nombre completo del estudiante (desnormalizado)
     */
    private String nombreEstudiante;

    /**
     * Nombre de la empresa (desnormalizado)
     */
    private String nombreEmpresa;

    /**
     * Modalidad de la pasantía
     */
    private String modalidad;

    /**
     * Indica si la postulación puede ser editada
     */
    private Boolean esEditable;

    /**
     * Fecha de última actualización
     */
    private LocalDate fechaActualizacion;
}
