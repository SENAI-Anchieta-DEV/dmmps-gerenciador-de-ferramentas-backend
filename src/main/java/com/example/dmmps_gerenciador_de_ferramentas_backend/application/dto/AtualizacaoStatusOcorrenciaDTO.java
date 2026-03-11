package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusOcorrencia;

public record AtualizacaoStatusOcorrenciaDTO(
        StatusOcorrencia statusOcorrencia,
        String justificativaDescarte
) {
}
