package com.seminario.pasantias.dto.response;

import com.seminario.pasantias.entity.EstadoPasantia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta básica para Pasantía.
 * Contiene información esencial sin relaciones anidadas.
 * Ideal para listados y búsquedas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasantiaResponseDTO {

    /**
     * ID único de la pasantía
     */
    private Integer idPasantia;

    /**
     * Título de la pasantía
     */
    private String titulo;

    /**
     * Puesto a cubrir
     */
    private String puestoACubrir;

    /**
     * Ciudad donde se realiza
     */
    private String ciudad;

    /**
     * Modalidad: Presencial, Híbrida, Remoto
     */
    private String modalidad;

    /**
     * Monto de asignación/estímulo
     */
    private Float asignacionEstimulo;

    /**
     * Cantidad total de pasantes solicitados
     */
    private Integer cantidadDePasantes;

    /**
     * Fecha de publicación
     */
    private LocalDate fechaPublicacion;

    /**
     * Fecha de caducidad
     */
    private LocalDate fechaCaducidad;

    /**
     * Estado actual de la pasantía
     */
    private EstadoPasantia estado;

    /**
     * Email de contacto
     */
    private String emailContacto;

    /**
     * ID de la empresa que publica
     */
    private Integer idEmpresa;

    /**
     * Nombre de la empresa (desnormalizado para eficiencia)
     */
    private String nombreEmpresa;

    /**
     * Cantidad de postulaciones recibidas (calculado)
     */
    private Integer cantidadPostulaciones;

    /**
     * Indica si la pasantía está activa y acepta postulaciones
     */
    private Boolean aceptaPostulaciones;

    /**
     * Días restantes hasta la caducidad (calculado)
     */
    private Long diasRestantes;
}
