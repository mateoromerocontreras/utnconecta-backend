package com.seminario.pasantias.dto.request;

import com.seminario.pasantias.entity.EstadoPostulacion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para crear o actualizar una Postulación.
 * Representa la solicitud de un estudiante para una pasantía.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostulacionRequestDTO {

    /**
     * Fecha de la postulación (opcional, se asigna automáticamente si no se proporciona)
     */
    @PastOrPresent(message = "La fecha de postulación no puede ser futura")
    private LocalDate fechaPostulacion;

    /**
     * Fecha de inicio del contrato (opcional en creación, obligatorio al aceptar)
     */
    @Future(message = "La fecha de inicio del contrato debe ser futura")
    private LocalDate fechaInicioContrato;

    /**
     * Duración en meses del contrato (opcional en creación)
     */
    @Min(value = 1, message = "La duración debe ser al menos de 1 mes")
    @Max(value = 24, message = "La duración no puede exceder 24 meses")
    private Integer duracionMeses;

    /**
     * Estado de la postulación (opcional, por defecto BORRADOR)
     */
    private EstadoPostulacion estado;

    /**
     * ID de la pasantía a la que se postula (obligatorio)
     */
    @NotNull(message = "El ID de la pasantía es obligatorio")
    @Positive(message = "El ID de la pasantía debe ser positivo")
    private Integer idPasantia;

    /**
     * ID del estudiante que postula (obligatorio)
     */
    @NotNull(message = "El ID del estudiante es obligatorio")
    @Positive(message = "El ID del estudiante debe ser positivo")
    private Integer idEstudiante;

    /**
     * Valida que si hay fecha de inicio de contrato, también haya duración
     */
    @AssertTrue(message = "Si hay fecha de inicio de contrato, debe especificarse la duración")
    public boolean isContratoCompleto() {
        if (fechaInicioContrato == null && duracionMeses == null) {
            return true;
        }
        if (fechaInicioContrato != null && duracionMeses != null) {
            return true;
        }
        return false;
    }
}
