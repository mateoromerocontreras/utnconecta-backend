package com.seminario.pasantias.config;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Objects.nonNull;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {
    @Value("${spring.application.name}")
    private String title;
    @Value("${apiinfo.description}")
    private String description;

    @Value("${apiinfo.version}")
    private String version;
    @Bean
    public OpenAPI openAPI() {

        final var info = new Info()
                .title(title)
                .version(version)
                .description(description);

        return new OpenAPI()
                .info(info);
    }
    @Bean
    public OpenApiCustomizer openApiCustomiser() {
        return openApi -> {
            List<Server> servers = openApi.getServers();
            if (nonNull(servers)) {
                servers.stream()
                        .filter(x -> !x.getUrl().contains("localhost"))
                        .forEach(x -> {
                            x.setUrl(x.getUrl().replace("http", "https"));
                            x.setDescription(null);
                        });
            }
        };
    }


}
