package com.seminario.pasantias.service;

import com.seminario.pasantias.entity.EmailVerificationToken;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.persistence.EmailVerificationTokenMapper;
import com.seminario.pasantias.persistence.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationService {

    @Autowired
    private EmailVerificationTokenMapper tokenMapper;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private EmailService emailService;

    @Value("${app.verification.base-url:http://localhost:8080/auth/confirmar?token=}")
    private String verificationBaseUrl;

    @Value("${app.verification.expiration-hours:24}")
    private long expirationHours;

    public void enviarCorreoDeVerificacion(Usuario usuario) {
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setIdUsuario(usuario.getIdUsuario());
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setFechaExpiracion(LocalDateTime.now().plusHours(expirationHours));
        verificationToken.setUsado(false);
        verificationToken.setFechaCreacion(LocalDateTime.now());
        tokenMapper.insert(verificationToken);

        String verificationLink = verificationBaseUrl + verificationToken.getToken();

        emailService.enviarEmailConfirmacion(usuario.getEmail(), verificationLink);
    }

    public void confirmarToken(String token) {
        Optional<EmailVerificationToken> tokenOpt = tokenMapper.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Token de verificación inválido");
        }

        EmailVerificationToken storedToken = tokenOpt.get();
        if (Boolean.TRUE.equals(storedToken.getUsado())) {
            throw new RuntimeException("El token ya fue utilizado");
        }

        if (storedToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        usuarioMapper.activate(storedToken.getIdUsuario());
        tokenMapper.markAsUsed(storedToken.getIdToken());
    }
}
