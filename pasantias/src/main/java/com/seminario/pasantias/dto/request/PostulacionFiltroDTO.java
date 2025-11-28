package com.seminario.pasantias.dto.request;

import com.seminario.pasantias.entity.EstadoPostulacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para filtrar búsquedas de Postulaciones.
 * Todos los campos son opcionales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostulacionFiltroDTO {

    /**
     * Filtrar por ID de estudiante
     */
    private Integer idEstudiante;

    /**
     * Filtrar por ID de pasantía
     */
    private Integer idPasantia;

    /**
     * Filtrar por estado
     */
    private EstadoPostulacion estado;

    /**
     * Fecha de postulación desde
     */
    private LocalDate fechaDesde;

    /**
     * Fecha de postulación hasta
     */
    private LocalDate fechaHasta;

    /**
     * Filtrar por ID de empresa
     */
    private Integer idEmpresa;

    /**
     * Solo postulaciones activas (no FINALIZADA)
     */
    private Boolean soloActivas;

    /**
     * Ordenar por (fechaPostulacion, estado, etc.)
     */
    private String ordenarPor;

    /**
     * Dirección de ordenamiento (ASC, DESC)
     */
    private String direccion;

    /**
     * Número de página (para paginación)
     */
    private Integer pagina;

    /**
     * Tamaño de página (para paginación)
     */
    private Integer tamanio;
}
