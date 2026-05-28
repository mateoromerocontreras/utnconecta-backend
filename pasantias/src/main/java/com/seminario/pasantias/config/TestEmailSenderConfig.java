package com.seminario.pasantias.config;

import com.seminario.pasantias.service.EmailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Test profile wiring: provide a no-op EmailSender so that
 * email verification flows don't require SMTP during integration tests.
 */
@Configuration
@Profile("test")
public class TestEmailSenderConfig {

    @Bean
    public EmailSender emailSender() {
        return (to, token) -> {
            // no-op in test profile
        };
    }
}

