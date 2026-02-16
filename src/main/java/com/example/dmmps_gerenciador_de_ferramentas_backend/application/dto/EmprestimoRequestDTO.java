package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import java.util.UUID;

public record EmprestimoRequestDTO(
        UUID usuarioId,    // Quem está a levar
        UUID ferramentaId  // O que está a ser levado
){
}
