package com.seminario.pasantias.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsuarioRequest {
    private String idUsuario;
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private Boolean activo;
}