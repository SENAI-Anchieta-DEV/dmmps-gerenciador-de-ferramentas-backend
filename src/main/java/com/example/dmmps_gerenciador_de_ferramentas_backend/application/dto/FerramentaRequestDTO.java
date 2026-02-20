package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

public record FerramentaRequestDTO(
        String nome,
        String descricao,
        String fabricante,
        String codigoPatrimonio,
        String gavetaLocalizacao
) {
}