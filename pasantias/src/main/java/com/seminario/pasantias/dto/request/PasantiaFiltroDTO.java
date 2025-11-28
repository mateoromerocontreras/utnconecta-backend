package com.seminario.pasantias.dto.request;

import com.seminario.pasantias.entity.EstadoPasantia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para filtrar búsquedas de Pasantías.
 * Todos los campos son opcionales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasantiaFiltroDTO {

    /**
     * Búsqueda por texto en título o puesto
     */
    private String busqueda;

    /**
     * Filtrar por ciudad
     */
    private String ciudad;

    /**
     * Filtrar por modalidad
     */
    private String modalidad;

    /**
     * Filtrar por estado
     */
    private EstadoPasantia estado;

    /**
     * Filtrar por ID de empresa
     */
    private Integer idEmpresa;

    /**
     * Filtrar por IDs de carreras
     */
    private List<Integer> idsCarreras;

    /**
     * Asignación mínima
     */
    private Float asignacionMinima;

    /**
     * Asignación máxima
     */
    private Float asignacionMaxima;

    /**
     * Fecha de publicación desde
     */
    private LocalDate fechaPublicacionDesde;

    /**
     * Fecha de publicación hasta
     */
    private LocalDate fechaPublicacionHasta;

    /**
     * Solo pasantías activas (PUBLICADA)
     */
    private Boolean soloActivas;

    /**
     * Ordenar por (titulo, fechaPublicacion, asignacionEstimulo, etc.)
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
