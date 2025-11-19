package com.seminario.pasantias.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postulacion {
    private Integer idPostulacion;
    private LocalDate fechaPostulacion;
    private LocalDate fechaInicioContrato;
    private LocalDate fechaCreacion;
    private Integer duracionMeses;
    private EstadoPostulacion estado;
    private Pasantia pasantia;
    private Estudiante estudiante;
    private Integer idCv;
    private String observaciones;
}
