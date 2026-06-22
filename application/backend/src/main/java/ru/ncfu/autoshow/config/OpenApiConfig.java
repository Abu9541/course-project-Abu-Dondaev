package ru.ncfu.autoshow.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Конфигурация OpenAPI/Swagger UI с поддержкой JWT-авторизации. */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI autoshowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ИС «Автосалон» — REST API")
                        .version("1.0.0")
                        .description("""
                                Серверная часть мобильного приложения информационной системы автосалона.
                                Траектория В (мобильная разработка), архитектура PCMEF.
                                Для защищённых эндпоинтов нажмите «Authorize» и вставьте JWT-токен,
                                полученный через POST /api/auth/login.""")
                        .contact(new Contact().name("Дондаев Абу Умар-Пашаевич").email("dondaevabu126@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
