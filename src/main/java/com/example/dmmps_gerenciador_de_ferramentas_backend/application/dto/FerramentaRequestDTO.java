package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro ou atualização de uma ferramenta")
public record FerramentaRequestDTO(

        @Schema(description = "Nome da ferramenta", example = "Chave de Fenda Phillips")
        @NotBlank(message = "O nome da ferramenta é obrigatório")
        String nome,

        @Schema(description = "Descrição detalhada da ferramenta", example = "Chave de fenda cabeça Phillips tamanho 2")
        String descricao,

        @Schema(description = "Nome do fabricante", example = "Tramontina")
        @NotBlank(message = "O nome do fabricante é obrigatório")
        String fabricante,

        @Schema(description = "Código de patrimônio único da ferramenta", example = "PAT-001")
        @NotBlank(message = "O código de patrimônio é obrigatório")
        @Size(min = 3, max = 50, message = "O código de patrimônio deve ter entre 3 e 50 caracteres")
        String codigoPatrimonio,

        @Schema(description = "Localização da gaveta onde a ferramenta é armazenada", example = "A3")
        @NotNull(message = "A localização da gaveta é obrigatória")
        String gavetaLocalizacao
) {}