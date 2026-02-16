package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OcorrenciaResponseDTO(
        UUID id,
        UUID ferramentaId,
        UUID usuarioId,
        String titulo,
        String descricao,
        LocalDateTime dataAbertura
) {
}
