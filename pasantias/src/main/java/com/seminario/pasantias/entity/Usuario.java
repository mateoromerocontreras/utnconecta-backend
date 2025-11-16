package com.seminario.pasantias.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Integer idUsuario;
    private String username;
    private String email;
    private String password;
    private Integer idRol;
    private Rol rol; // Para carga eager de datos del rol
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    
    // Campos para verificación de email
    private Boolean emailVerificado;
    private String tokenVerificacion;
    private LocalDateTime fechaExpiracionToken;
}