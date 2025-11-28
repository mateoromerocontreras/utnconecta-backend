package com.seminario.pasantias.service;

import com.seminario.pasantias.entity.EmailVerificationToken;
import com.seminario.pasantias.entity.Usuario;
import com.seminario.pasantias.persistence.EmailVerificationTokenMapper;
import com.seminario.pasantias.persistence.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private JavaMailSender mailSender;

    @Value("${app.verification.base-url:http://localhost:8080/auth/confirmar?token=}")
    private String verificationBaseUrl;

    @Value("${app.verification.expiration-hours:24}")
    private long expirationHours;

    @Value("${app.mail.from:no-reply@pasantias.local}")
    private String defaultFrom;

    public void enviarCorreoDeVerificacion(Usuario usuario) {
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setIdUsuario(usuario.getIdUsuario());
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setFechaExpiracion(LocalDateTime.now().plusHours(expirationHours));
        verificationToken.setUsado(false);
        verificationToken.setFechaCreacion(LocalDateTime.now());
        tokenMapper.insert(verificationToken);

        String verificationLink = verificationBaseUrl + verificationToken.getToken();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(defaultFrom);
        message.setTo(usuario.getEmail());
        message.setSubject("Confirma tu cuenta");
        message.setText(
                "Hola " + usuario.getUsername() + ",\n\n" +
                "Para activar tu cuenta, hacé click o copia el siguiente enlace en tu navegador:\n" +
                verificationLink + "\n\n" +
                "Este enlace vence en " + expirationHours + " horas.\n" +
                "Si no creaste una cuenta, ignorá este correo."
        );
        mailSender.send(message);
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
