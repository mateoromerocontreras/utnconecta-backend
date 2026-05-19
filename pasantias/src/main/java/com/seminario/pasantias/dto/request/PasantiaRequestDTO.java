package com.seminario.pasantias.dto.request;

import com.seminario.pasantias.entity.EstadoPasantia;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear o actualizar una Pasantía.
 * Incluye validaciones de negocio para asegurar datos correctos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasantiaRequestDTO {

    /**
     * Título de la pasantía (obligatorio, 5-200 caracteres)
     */
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    private String titulo;

    /**
     * Nombre del puesto a cubrir (obligatorio)
     */
    @NotBlank(message = "El puesto a cubrir es obligatorio")
    @Size(max = 150, message = "El puesto no puede exceder 150 caracteres")
    private String puestoACubrir;

    /**
     * Ciudad donde se realiza la pasantía (obligatorio)
     */
    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    /**
     * Modalidad: Presencial, Híbrida, Remoto (obligatorio)
     */
    @NotBlank(message = "La modalidad es obligatoria")
    @Pattern(regexp = "^(Presencial|Híbrida|Remoto)$", 
             message = "La modalidad debe ser: Presencial, Híbrida o Remoto")
    private String modalidad;

    /**
     * Monto de la asignación/estímulo (opcional, debe ser positivo si se proporciona)
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "La asignación debe ser mayor a 0")
    private Float asignacionEstimulo;

    /**
     * Cantidad de pasantes a contratar (obligatorio, al menos 1)
     */
    @NotNull(message = "La cantidad de pasantes es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 pasante")
    @Max(value = 50, message = "No se pueden solicitar más de 50 pasantes")
    private Integer cantidadDePasantes;

    /**
     * Fecha de publicación (obligatorio, no puede ser en el pasado)
     */
    @NotNull(message = "La fecha de publicación es obligatoria")
    @FutureOrPresent(message = "La fecha de publicación no puede ser en el pasado")
    private LocalDate fechaPublicacion;

    /**
     * Fecha de caducidad (obligatorio, debe ser posterior a fecha de publicación)
     */
    @NotNull(message = "La fecha de caducidad es obligatoria")
    @Future(message = "La fecha de caducidad debe ser futura")
    private LocalDate fechaCaducidad;

    /**
     * Estado inicial de la pasantía (opcional, por defecto PUBLICADA)
     */
    private EstadoPasantia estado;

    /**
     * ID de la empresa que publica (obligatorio)
     */
    @NotNull(message = "El ID de la empresa es obligatorio")
    @Positive(message = "El ID de la empresa debe ser positivo")
    private Integer idEmpresa;

    /**
     * Lista de IDs de carreras elegibles (obligatorio, al menos 1)
     */
    @NotEmpty(message = "Debe haber al menos una carrera asociada")
    @Size(min = 1, max = 20, message = "Debe haber entre 1 y 20 carreras")
    private List<@Positive(message = "Los IDs de carrera deben ser positivos") Integer> idsCarreras;

    /**
     * Email de contacto (obligatorio, debe ser válido)
     */
    @NotBlank(message = "El email de contacto es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String emailContacto;

    /**
     * Conocimientos requeridos (opcional)
     */
    @Size(max = 2000, message = "Los conocimientos no pueden exceder 2000 caracteres")
    private String conocimientos;

    /**
     * Otros requisitos (opcional)
     */
    @Size(max = 2000, message = "Los otros requisitos no pueden exceder 2000 caracteres")
    private String otrosRequisitos;

    /**
     * Beneficios ofrecidos (opcional)
     */
    @Size(max = 2000, message = "Los beneficios no pueden exceder 2000 caracteres")
    private String beneficios;

    /**
     * Valida que la fecha de caducidad sea posterior a la fecha de publicación
     */
    @AssertTrue(message = "La fecha de caducidad debe ser posterior a la fecha de publicación")
    public boolean isFechaCaducidadValida() {
        if (fechaPublicacion == null || fechaCaducidad == null) {
            return true; // Se validará con @NotNull
        }
        return fechaCaducidad.isAfter(fechaPublicacion);
    }
}
