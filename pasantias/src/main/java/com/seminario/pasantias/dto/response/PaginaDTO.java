package com.seminario.pasantias.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas.
 * Encapsula datos de paginación junto con el contenido.
 *
 * @param <T> Tipo de datos contenidos en la página
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginaDTO<T> {

    /**
     * Lista de elementos en la página actual
     */
    private List<T> contenido;

    /**
     * Número de página actual (base 0)
     */
    private Integer paginaActual;

    /**
     * Tamaño de página (elementos por página)
     */
    private Integer tamanioPagina;

    /**
     * Total de elementos en todas las páginas
     */
    private Long totalElementos;

    /**
     * Total de páginas disponibles
     */
    private Integer totalPaginas;

    /**
     * Indica si es la primera página
     */
    private Boolean esPrimeraPagina;

    /**
     * Indica si es la última página
     */
    private Boolean esUltimaPagina;

    /**
     * Indica si hay página anterior
     */
    private Boolean tienePaginaAnterior;

    /**
     * Indica si hay página siguiente
     */
    private Boolean tienePaginaSiguiente;

    /**
     * Número de elementos en esta página
     */
    private Integer elementosEnPagina;
}
