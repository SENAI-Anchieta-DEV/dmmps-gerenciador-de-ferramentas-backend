package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT retornado após autenticação bem-sucedida")
public record TokenResponseDTO(

        @Schema(description = "Token JWT para uso no header Authorization",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}
