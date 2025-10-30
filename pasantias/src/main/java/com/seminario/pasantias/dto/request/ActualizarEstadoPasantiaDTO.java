package com.seminario.pasantias.dto.request;

import com.seminario.pasantias.entity.EstadoPasantia;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar el estado de una Pasantía.
 * Permite cambios de estado con validaciones específicas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoPasantiaDTO {

    /**
     * Nuevo estado de la pasantía (obligatorio)
     */
    @NotNull(message = "El estado es obligatorio")
    private EstadoPasantia estado;

    /**
     * Motivo o comentario del cambio de estado (opcional)
     */
    private String motivo;
}
