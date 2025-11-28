package com.seminario.pasantias.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pasantia {
    private Integer idPasantia;
    private String titulo;
    private String puestoACubrir;
    private String ciudad;
    private String modalidad;
    private Float asignacionEstimulo;
    private Integer cantidadDePasantes;
    private LocalDate fechaPublicacion;
    private LocalDate fechaCaducidad;
    private EstadoPasantia estado;
    private Empresa empresa;
    private List<Carrera> carreras;
    private List<Postulacion> postulaciones;
    private String emailContacto;
    private String conocimientos;
    private String otrosRequisitos;
    private String beneficios;
}
