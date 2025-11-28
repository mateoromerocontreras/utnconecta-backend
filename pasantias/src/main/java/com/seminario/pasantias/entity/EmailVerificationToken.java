package com.seminario.pasantias.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationToken {
    private Integer idToken;
    private Integer idUsuario;
    private String token;
    private LocalDateTime fechaExpiracion;
    private Boolean usado;
    private LocalDateTime fechaCreacion;
}
