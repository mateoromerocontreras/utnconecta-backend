package com.seminario.pasantias.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@org.springframework.lang.NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "http://localhost.localdomain:3000",
                        "http://localhost:5173", "http://localhost.localdomain:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Authorization", "Content-Type", "Accept")
                        .maxAge(3600);
            }
            
            @Override
            public void extendMessageConverters(@org.springframework.lang.NonNull List<HttpMessageConverter<?>> converters) {
                // Asegurar que todos los converters usen UTF-8
                for (int i = 0; i < converters.size(); i++) {
                    HttpMessageConverter<?> converter = converters.get(i);

                    if (converter instanceof StringHttpMessageConverter stringConverter) {
                        if (!StandardCharsets.UTF_8.equals(stringConverter.getDefaultCharset())) {
                            StringHttpMessageConverter utf8Converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
                            utf8Converter.setWriteAcceptCharset(false);
                            converters.set(i, utf8Converter);
                        }
                    }

                    if (converter instanceof MappingJackson2HttpMessageConverter jsonConverter) {
                        if (!StandardCharsets.UTF_8.equals(jsonConverter.getDefaultCharset())) {
                            jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
                        }
                    }
                }
            }
        };
    }
}