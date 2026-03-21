package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.EstadoConservacao;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para realizar o check-in (devolução) de uma ferramenta")
public record DevolucaoRequestDTO(

        @Schema(description = "Estado de conservação da ferramenta no momento da devolução",
                example = "BOM_ESTADO", allowableValues = {"BOM_ESTADO", "DANIFICADA"})
        EstadoConservacao estadoConservacao
) {}
