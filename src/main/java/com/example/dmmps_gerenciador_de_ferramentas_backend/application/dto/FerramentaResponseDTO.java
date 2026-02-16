package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;

import java.util.UUID;

public record FerramentaResponseDTO(
        UUID id,
        String nome,
        String descricao,
        String fabricante,
        String codigoQr,
        StatusFerramenta status,
        String gavetaLocalizacao
) {
}
