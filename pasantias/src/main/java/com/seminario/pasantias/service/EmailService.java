package com.seminario.pasantias.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio para el envío de emails del sistema
 */
@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;
    
    /**
     * Envía un email de confirmación de cuenta
     * 
     * @param toEmail Email del destinatario
     * @param token Token de verificación
     */
    public void enviarEmailConfirmacion(String toEmail, String token) throws MailException {
        String confirmationUrl = frontendUrl + "/confirmar-cuenta?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Confirma tu cuenta - Sistema de Pasantías");
        message.setText(
            "¡Bienvenido al Sistema de Pasantías!\n\n" +
            "Para activar tu cuenta, por favor haz clic en el siguiente enlace:\n\n" +
            confirmationUrl + "\n\n" +
            "Este enlace expirará en 24 horas.\n\n" +
            "Si no creaste esta cuenta, puedes ignorar este email.\n\n" +
            "Saludos,\n" +
            "Equipo de Pasantías"
        );
        
        try {
            mailSender.send(message);
            log.info("Email de confirmación enviado exitosamente a: {}", toEmail);
        } catch (MailException e) {
            log.warn("Error al enviar email de confirmación a {}: {}", toEmail, e.getMessage());
            log.debug("Causa: {}", e.getCause() != null ? e.getCause().getMessage() : "Desconocida");
            throw e;
        }
    }
}

