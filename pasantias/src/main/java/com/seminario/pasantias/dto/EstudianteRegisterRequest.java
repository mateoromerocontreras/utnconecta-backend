package com.seminario.pasantias.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteRegisterRequest {
    private String nombre;
    private String apellido;
    private String dni;
    private String telCelular;
    private String email;
    private String password;
}