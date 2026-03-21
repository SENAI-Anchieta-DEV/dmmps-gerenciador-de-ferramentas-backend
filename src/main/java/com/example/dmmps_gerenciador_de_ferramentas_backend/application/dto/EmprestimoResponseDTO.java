package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de retorno de um empréstimo")
public record EmprestimoResponseDTO(

        @Schema(description = "ID do empréstimo", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID do técnico responsável", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID usuarioId,

        @Schema(description = "Nome do técnico responsável", example = "Técnico Silva")
        String nomeUsuario,

        @Schema(description = "ID da ferramenta emprestada", example = "987fcdeb-51a2-43f7-b210-111122223333")
        UUID ferramentaId,

        @Schema(description = "Nome da ferramenta emprestada", example = "Chave de Fenda Phillips")
        String nomeFerramenta,

        @Schema(description = "Código de patrimônio da ferramenta", example = "PAT-001")
        String codigoPatrimonio,

        @Schema(description = "Data e hora da retirada", example = "2025-03-21T08:00:00")
        LocalDateTime dataRetirada,

        @Schema(description = "Data e hora da devolução. Null enquanto em aberto.", example = "2025-03-21T17:00:00", nullable = true)
        LocalDateTime dataDevolucao,

        @Schema(description = "Estado de conservação informado na devolução. Null enquanto em aberto.",
                example = "BOM_ESTADO", allowableValues = {"BOM_ESTADO", "DANIFICADA"}, nullable = true)
        String estadoConservacao,

        @Schema(description = "Status atual do empréstimo",
                example = "ABERTO", allowableValues = {"ABERTO", "FINALIZADO"})
        String status
) {}
