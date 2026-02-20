package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record FerramentaResponseDTO(
        UUID id,
        @NotBlank(message = "O nome da ferramenta é obrigatório")
        String nome,
        String descricao,
        @NotBlank(message = "O nome do fabricante é obrigatório")
        String fabricante,
        @NotBlank(message = "O código de patrimônio é obrigatório")
        @Size(min = 3, max = 50, message = "O código de património deve ter entre 3 e 50 caracteres")
        String codigoPatrimonio,
        StatusFerramenta status,
        @NotNull(message = "A localização da gaveta é obrigatória")
        String gavetaLocalizacao
) {
}
