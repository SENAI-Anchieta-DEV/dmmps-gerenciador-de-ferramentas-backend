package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.PerfilUsuario;

public record UsuarioRequestDTO(
        String nome,
        String email,
        String senha,
        String registro, // Identificação da empresa
        PerfilUsuario perfil
) {
}
