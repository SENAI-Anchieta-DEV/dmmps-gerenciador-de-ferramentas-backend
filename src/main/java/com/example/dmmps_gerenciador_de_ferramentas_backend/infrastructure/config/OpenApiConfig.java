package com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SENAI ToolHub — API de Gerenciamento de Ferramentas")
                        .description("""
                                API REST do sistema **DMMPS ToolHub**, desenvolvido como Projeto Integrador
                                no curso Técnico em Desenvolvimento de Sistemas — SENAI Anchieta.
                                
                                Gerencia o ciclo de vida de ferramentas (check-out/check-in), ocorrências,
                                usuários e integração com dispositivos IoT via MQTT/ESP32.
                                
                                **Autenticação:** envie o token JWT no header `Authorization: Bearer <token>`.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipe DMMPS — SENAI Anchieta")
                                .email("admin@toolhub.com")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Informe o token JWT obtido em POST /auth/login")));
    }
}
