package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmprestimoResponseDTO(
        UUID id,
        UUID usuarioId,
        UUID ferramentaId,
        LocalDateTime dataRetirada,
        LocalDateTime dataDevolucao,
        String status // ATIVO, FINALIZADO
) {
}
