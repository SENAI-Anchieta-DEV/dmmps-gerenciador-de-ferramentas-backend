package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados para abertura de uma ocorrência de manutenção")
public record OcorrenciaRequestDTO(

        @Schema(description = "ID da ferramenta com problema", example = "987fcdeb-51a2-43f7-b210-111122223333")
        UUID ferramentaId,

        @Schema(description = "Título resumido da ocorrência", example = "Chave com cabo quebrado")
        String titulo,

        @Schema(description = "Descrição detalhada do problema identificado",
                example = "O cabo da chave de fenda está rachado e pode causar acidentes.")
        String descricao
) {}
