package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciais para autenticação")
public record LoginRequestDTO(

        @Schema(description = "E-mail cadastrado do usuário", example = "admin@toolhub.com")
        String email,

        @Schema(description = "Senha do usuário", example = "senha123")
        String senha
) {}
