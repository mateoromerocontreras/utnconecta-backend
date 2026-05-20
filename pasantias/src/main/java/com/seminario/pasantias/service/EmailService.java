package com.seminario.pasantias.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementación de {@link EmailSender} para el perfil de desarrollo.
 * <p>
 * Estrategia híbrida:
 * <ul>
 *   <li><b>Consola:</b> Imprime la URL de verificación en el log para hacer Ctrl+Clic desde el IDE.</li>
 *   <li><b>Mailpit:</b> Envía el correo vía SMTP local (puerto 1025). Si Mailpit no está activo,
 *       el error se loguea sin interrumpir el flujo de registro.</li>
 * </ul>
 */
@Service
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.username:noreply@utnconecta.com}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String to, String token) {
        String confirmationUrl = frontendUrl + "/confirmar-cuenta?token=" + token;

        // ── Mundo 1: Consola (siempre disponible) ──────────────────────────
        log.info("""
                
                ╔══════════════════════════════════════════════════════════════╗
                ║              📧  EMAIL DE VERIFICACIÓN  📧                 ║
                ╠══════════════════════════════════════════════════════════════╣
                ║  Para:  {}
                ║  URL:   {}
                ╚══════════════════════════════════════════════════════════════╝
                """, to, confirmationUrl);

        // ── Mundo 2: Mailpit (tolerante a fallos) ──────────────────────────
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Confirma tu cuenta - UTN Conecta");
            message.setText(
                    "¡Bienvenido a UTN Conecta!\n\n" +
                    "Para activar tu cuenta, hacé clic en el siguiente enlace:\n\n" +
                    confirmationUrl + "\n\n" +
                    "Este enlace expirará en 24 horas.\n\n" +
                    "Si no creaste esta cuenta, podés ignorar este email.\n\n" +
                    "Saludos,\n" +
                    "Equipo UTN Conecta"
            );
            mailSender.send(message);
            log.info("✅ Email enviado exitosamente vía Mailpit a: {}", to);
        } catch (MailException e) {
            log.warn("⚠️  Mailpit no disponible. El email NO se envió a {}. Causa: {}", to, e.getMessage());
            log.debug("Detalle del error de Mailpit:", e);
        }
    }
}
