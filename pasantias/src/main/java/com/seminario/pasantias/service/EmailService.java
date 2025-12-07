package com.seminario.pasantias.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio para el envío de emails del sistema
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    /**
     * Envía un email de confirmación de cuenta
     * 
     * @param toEmail         Email del destinatario
     * @param confirmationUrl URL completa de confirmación
     */
    public void enviarEmailConfirmacion(String toEmail, String confirmationUrl) throws MailException {

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
                        "Equipo de Pasantías");

        try {
            mailSender.send(message);
            System.out.println("Email de confirmación enviado exitosamente a: " + toEmail);
        } catch (MailException e) {
            System.err.println("Error al enviar email de confirmación a " + toEmail + ": " + e.getMessage());
            System.err.println("Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "Desconocida"));
            throw e;
        }
    }
}
