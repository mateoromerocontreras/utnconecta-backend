package com.seminario.pasantias.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final String ROLE_ADMIN = "ADMINISTRADOR";
    private static final String ROLE_EMPRESA = "EMPRESA";
    private static final String ROLE_ESTUDIANTE = "ESTUDIANTE";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Stateless API with JWT Bearer tokens (no session/cookie auth) → CSRF protection is not applicable.
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                // Avoid "*" + credentials (browsers will reject it and it's unsafe).
                corsConfiguration.setAllowedOrigins(List.of(
                        "http://localhost:3000",
                        "http://localhost:5173"
                ));
                corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
            }))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/registrarUsuario").permitAll()
                .requestMatchers("/estudiantes/crearEstudiante").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/notificaciones/**").permitAll()
                // Endpoints de usuarios: solo administradores (después de los públicos)
                .requestMatchers("/usuarios/**").hasRole(ROLE_ADMIN)
                // Endpoints de empresas: GET público, resto protegido
                .requestMatchers(HttpMethod.GET, "/empresas", "/empresas/consultarEmpresas").permitAll()
                .requestMatchers("/empresas/**").hasAnyRole(ROLE_ADMIN, ROLE_EMPRESA)
                // Endpoints de pasantías: GET público para listar todas, publicadas y ver detalles, resto protegido
                .requestMatchers(HttpMethod.GET, "/pasantias").permitAll()
                .requestMatchers(HttpMethod.GET, "/pasantias/publicadas").permitAll()
                .requestMatchers(HttpMethod.GET, "/pasantias/*").permitAll()
                // Endpoints de carreras: GET público, resto protegido
                .requestMatchers(HttpMethod.GET, "/carreras/consultarCarrera").permitAll()
                .requestMatchers(HttpMethod.GET, "/carreras/listarCarreras").permitAll()
                .requestMatchers("/carreras/**").hasRole(ROLE_ADMIN)
                .requestMatchers("/roles/**").hasRole(ROLE_ADMIN)
                // Estudiantes endpoints
                .requestMatchers("/estudiantes/**").hasAnyRole(ROLE_ADMIN, ROLE_ESTUDIANTE)
                // Pasantías endpoints: buscar y ver públicas es público, resto se controla con @PreAuthorize
                .requestMatchers(HttpMethod.GET, "/api/pasantias/publicadas").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pasantias/buscar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pasantias/{id}").permitAll()
                .requestMatchers("/api/pasantias/**").hasAnyRole(ROLE_ADMIN, ROLE_EMPRESA)
                // Endpoint para registrar pasantía: requiere autenticación y validación de empresa
                .requestMatchers(HttpMethod.POST, "/pasantias/registrar").hasAnyRole(ROLE_ADMIN, ROLE_EMPRESA)
                // Endpoint para aprobar pasantía: solo ADMINISTRADOR
                .requestMatchers(HttpMethod.PUT, "/pasantias/*/aprobar").hasRole(ROLE_ADMIN)
                // Endpoint para finalizar pasantía: solo ADMINISTRADOR
                .requestMatchers(HttpMethod.PUT, "/pasantias/*/finalizar").hasRole(ROLE_ADMIN)
                // OPTIONS requests para CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                //POSTULACIONES ADMINISTRADOR O ESTUDIANTES
                .requestMatchers("/postulaciones/registrarPostulacion").hasAnyRole(ROLE_ADMIN, ROLE_ESTUDIANTE)
                .requestMatchers("/postulaciones/consultarPostulaciones").hasAnyRole(ROLE_ADMIN, ROLE_ESTUDIANTE)
                // Cualquier otra solicitud requiere autenticación
                .requestMatchers("/cvs/**").hasAnyRole(ROLE_ADMIN, ROLE_ESTUDIANTE)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}