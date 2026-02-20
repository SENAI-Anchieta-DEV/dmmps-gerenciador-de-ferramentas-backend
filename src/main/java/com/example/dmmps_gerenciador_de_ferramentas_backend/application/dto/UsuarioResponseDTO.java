package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        @NotBlank(message = "O nome é obrigatório")
        String nome,
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,
        @NotBlank(message = "O registro é obrigatório")
        String registro,
        PerfilUsuario perfil,
        Boolean ativo
) {
}
