package com.seminario.pasantias.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Servicio para generar y validar tokens de verificación
 */
@Service
public class TokenService {
    
    private static final int TOKEN_LENGTH = 32;
    private static final int VERIFICATION_TOKEN_EXPIRATION_HOURS = 24;
    
    /**
     * Genera un token seguro aleatorio
     * 
     * @return Token codificado en Base64 URL-safe
     */
    public String generarToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[TOKEN_LENGTH];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    /**
     * Calcula la fecha de expiración para token de verificación (24 horas)
     * 
     * @return Fecha de expiración
     */
    public LocalDateTime calcularExpiracionVerificacion() {
        return LocalDateTime.now().plusHours(VERIFICATION_TOKEN_EXPIRATION_HOURS);
    }
    
    /**
     * Verifica si un token ha expirado
     * 
     * @param fechaExpiracion Fecha de expiración del token
     * @return true si el token ha expirado, false en caso contrario
     */
    public boolean tokenExpirado(LocalDateTime fechaExpiracion) {
        return fechaExpiracion == null || LocalDateTime.now().isAfter(fechaExpiracion);
    }
}

