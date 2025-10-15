package com.seminario.pasantias.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contacto {
    private Integer idContacto;
    private String nombre;
    private String apellido;
    private String emailResponsable;
    private String telefonoResponsable;
    private Integer idEmpresa;
}