package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.EstadoConservacao;

// DTO de entrada para check-in (devolução) (RF12, RN03)
public record DevolucaoRequestDTO(
        EstadoConservacao estadoConservacao
) {
}
