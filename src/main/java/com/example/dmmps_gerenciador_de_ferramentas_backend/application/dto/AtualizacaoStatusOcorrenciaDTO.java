package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusOcorrencia;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para atualização do status de uma ocorrência")
public record AtualizacaoStatusOcorrenciaDTO(

        @Schema(description = "Novo status da ocorrência",
                example = "RESOLVIDA",
                allowableValues = {"EM_MANUTENCAO", "RESOLVIDA", "DESCARTADA"})
        StatusOcorrencia statusOcorrencia,

        @Schema(description = "Justificativa obrigatória quando o status for DESCARTADA (RN06)",
                example = "Ferramenta sem conserto viável, custo supera o valor do equipamento.",
                nullable = true)
        String justificativaDescarte
) {}
