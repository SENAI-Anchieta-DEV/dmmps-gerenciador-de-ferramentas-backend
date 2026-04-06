package com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.config;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Schemas reutilizáveis para documentação OpenAPI.
 * Representa o formato ProblemDetail retornado pelo GlobalExceptionHandler.
 */
public class OpenApiSchemas {

    @Schema(name = "ProblemDetail", description = "Formato padrão de erro da API (RFC 9457)")
    public record ProblemDetailSchema(

            @Schema(description = "Código HTTP do erro", example = "404")
            int status,

            @Schema(description = "Título curto do problema", example = "Recurso não encontrado")
            String title,

            @Schema(description = "Descrição detalhada do problema",
                    example = "Ferramenta não encontrada com id: 123e4567-e89b-12d3-a456-426614174000")
            String detail,

            @Schema(description = "URI do endpoint que gerou o erro",
                    example = "/ferramentas/123e4567-e89b-12d3-a456-426614174000")
            String instance,

            @Schema(description = "Momento em que o erro ocorreu", example = "2025-03-21T14:30:00")
            String timestamp,

            @Schema(description = "Identificador da aplicação", example = "GerenciadorFerramentasAPI")
            String application
    ) {}

    @Schema(name = "ValidationProblemDetail", description = "Erro de validação de campos (@Valid)")
    public record ValidationProblemDetailSchema(

            @Schema(example = "400")
            int status,

            @Schema(example = "Erro de validação")
            String title,

            @Schema(example = "Um ou mais campos estão inválidos")
            String detail,

            @Schema(description = "Mapa de campo → mensagem de erro",
                    example = "{\"nome\": \"O nome da ferramenta é obrigatório\", \"codigoPatrimonio\": \"O código de patrimônio é obrigatório\"}")
            Object errors
    ) {}
}
