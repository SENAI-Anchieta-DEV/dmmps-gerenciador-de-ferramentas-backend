package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import java.util.UUID;

public record OcorrenciaRequestDTO(
        UUID ferramentaId,
        UUID usuarioId, // Quem está relatando (Técnico ou Almoxarife)
        String titulo,
        String descricao
) {
}
