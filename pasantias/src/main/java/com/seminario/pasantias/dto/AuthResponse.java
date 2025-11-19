package com.seminario.pasantias.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String rol; // Nombre del rol
    private String message;
    
    public AuthResponse(String token, String username, String email, String rol) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.rol = rol;
        this.message = null;
    }
}