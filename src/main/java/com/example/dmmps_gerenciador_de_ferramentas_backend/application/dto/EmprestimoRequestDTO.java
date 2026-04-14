package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados para realizar o check-out de uma ferramenta")
public record EmprestimoRequestDTO(

        @Schema(description = "ID da ferramenta a ser retirada",
                example = "987fcdeb-51a2-43f7-b210-111122223333")
        UUID ferramentaId
) {}
