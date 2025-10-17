package com.seminario.pasantias.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class PostulacionRequest {
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Integer estudianteId;
    //@NotNull(message = "El ID de la pasantía es obligatorio")
    //private Integer pasantiaId; // faltaria agregar lo de pasantia
}
