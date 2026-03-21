package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusOcorrencia;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de retorno de uma ocorrência")
public record OcorrenciaResponseDTO(

        @Schema(description = "ID único da ocorrência", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID da ferramenta relacionada", example = "987fcdeb-51a2-43f7-b210-111122223333")
        UUID ferramentaId,

        @Schema(description = "ID do usuário que abriu a ocorrência", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID usuarioId,

        @Schema(description = "Título resumido da ocorrência", example = "Chave com cabo quebrado")
        String titulo,

        @Schema(description = "Descrição detalhada do problema",
                example = "O cabo da chave de fenda está rachado e pode causar acidentes.")
        String descricao,

        @Schema(description = "Status atual da ocorrência",
                example = "EM_MANUTENCAO",
                allowableValues = {"EM_MANUTENCAO", "RESOLVIDA", "DESCARTADA"})
        StatusOcorrencia statusOcorrencia,

        @Schema(description = "Data e hora de abertura da ocorrência", example = "2025-03-21T08:30:00")
        LocalDateTime dataAbertura
) {}
