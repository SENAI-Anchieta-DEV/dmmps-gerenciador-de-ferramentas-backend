package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusConectividade;

import java.util.UUID;

public record DispositivoIoTResponseDTO(
        UUID id,
        String macAddress,
        String localizacaoFisica,
        String descricao,
        StatusConectividade statusConectividade
) {
}
