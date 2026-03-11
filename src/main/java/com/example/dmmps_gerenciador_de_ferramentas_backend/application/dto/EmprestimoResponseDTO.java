package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmprestimoResponseDTO(
        UUID id,
        UUID usuarioId,
        String nomeUsuario,       // Nome legível do técnico
        UUID ferramentaId,
        String nomeFerramenta,    // Nome legível da ferramenta
        String codigoPatrimonio,  // Código de patrimônio da ferramenta
        LocalDateTime dataRetirada,
        LocalDateTime dataDevolucao,
        String estadoConservacao, // BOM_ESTADO, DANIFICADA — null enquanto em aberto
        String status // ATIVO, FINALIZADO
) {
}
