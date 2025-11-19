package com.seminario.pasantias.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {
    private Integer idEstudiante;
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
    private String email;
    private String telCelular;
    private String telFijo;
    private Integer idUsuario; // Relación con Usuario
    private Usuario usuario; // Para carga eager de datos del usuario
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
