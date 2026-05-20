package com.seminario.pasantias.service;

import com.seminario.pasantias.entity.EmailVerificationToken;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.persistence.EmailVerificationTokenMapper;
import com.seminario.pasantias.persistence.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de lógica de negocio para la verificación de email.
 * Se encarga de generar, persistir y validar tokens; delega el envío
 * físico del correo en la abstracción {@link EmailSender}.
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    public static class EmailVerificationException extends RuntimeException {
        public EmailVerificationException(String message) {
            super(message);
        }
    }

    private final EmailVerificationTokenMapper tokenMapper;
    private final UsuarioMapper usuarioMapper;
    private final EmailSender emailSender;

    @Value("${app.verification.expiration-hours:24}")
    private long expirationHours;

    /**
     * Genera un token de verificación, lo persiste y delega el envío del correo.
     */
    public void crearYEnviarToken(Usuario usuario) {
        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setIdUsuario(usuario.getIdUsuario());
        verificationToken.setToken(token);
        verificationToken.setFechaExpiracion(LocalDateTime.now().plusHours(expirationHours));
        verificationToken.setUsado(false);
        verificationToken.setFechaCreacion(LocalDateTime.now());
        tokenMapper.insert(verificationToken);

        emailSender.sendVerificationEmail(usuario.getEmail(), token);
    }

    /**
     * Valida un token de verificación y activa la cuenta del usuario.
     */
    public void confirmarToken(String token) {
        Optional<EmailVerificationToken> tokenOpt = tokenMapper.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new EmailVerificationException("Token de verificación inválido");
        }

        EmailVerificationToken storedToken = tokenOpt.get();
        if (Boolean.TRUE.equals(storedToken.getUsado())) {
            throw new EmailVerificationException("El token ya fue utilizado");
        }

        if (storedToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new EmailVerificationException("El token ha expirado");
        }

        usuarioMapper.activate(storedToken.getIdUsuario());
        tokenMapper.markAsUsed(storedToken.getIdToken());
    }
}
