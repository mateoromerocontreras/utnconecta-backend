package com.seminario.pasantias.service;

/**
 * Abstracción para el envío de emails de verificación.
 * Cada perfil de Spring (dev, prod, etc.) puede aportar su propia implementación.
 */
public interface EmailSender {
    void sendVerificationEmail(String to, String token);
}
