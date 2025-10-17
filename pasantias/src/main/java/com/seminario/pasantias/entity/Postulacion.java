package com.seminario.pasantias.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postulacion {
    private Integer idPostulacion;
    private LocalDateTime fecha;
    private String estado;
    private Integer idEstudiante;
    // private Integer idPasantia; // faltaria agregar lo de pasantia 
    private Estudiante estudiante;
    // private Pasantia pasantia; // faltaria agregar lo de pasantia
    private String observaciones;
    private LocalDateTime fechaActualizacion;
}
