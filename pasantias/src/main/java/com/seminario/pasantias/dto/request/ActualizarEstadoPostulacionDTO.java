package com.seminario.pasantias.dto.request;

import com.seminario.pasantias.entity.EstadoPostulacion;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para actualizar el estado de una Postulación.
 * Incluye datos adicionales necesarios según el nuevo estado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoPostulacionDTO {

    /**
     * Nuevo estado de la postulación (obligatorio)
     */
    @NotNull(message = "El estado es obligatorio")
    private EstadoPostulacion estado;

    /**
     * Fecha de inicio del contrato (obligatorio si estado = CUBIERTA)
     */
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDate fechaInicioContrato;

    /**
     * Duración en meses (obligatorio si estado = CUBIERTA)
     */
    @Min(value = 1, message = "La duración debe ser al menos de 1 mes")
    @Max(value = 24, message = "La duración no puede exceder 24 meses")
    private Integer duracionMeses;

    /**
     * Motivo o comentario del cambio de estado (opcional)
     */
    private String observaciones;
}
