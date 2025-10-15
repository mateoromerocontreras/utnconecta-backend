package com.seminario.pasantias.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteUpdateRequest {
    private String dni;
    private String apellido;
    private String nombre;
    private String especialidad;
    private String nroLegajo;
    private String calle;
    private Integer nroCalle;
    private String barrio;
    private String localidad;
    private String provincia;
    private String telCelular;
    private String telFijo;
}