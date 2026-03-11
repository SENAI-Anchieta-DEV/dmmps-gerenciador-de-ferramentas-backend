package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusOcorrencia;

import java.time.LocalDateTime;
import java.util.UUID;

public record OcorrenciaResponseDTO(
        UUID id,
        UUID ferramentaId,
        UUID usuarioId,
        String titulo,
        String descricao,
        StatusOcorrencia statusOcorrencia,
        LocalDateTime dataAbertura

) {
}
