package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Dados de retorno de um usuário")
public record UsuarioResponseDTO(

        @Schema(description = "ID único do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Nome completo do usuário", example = "João Silva")
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @Schema(description = "E-mail de login do usuário", example = "joao@toolhub.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @Schema(description = "Registro/matrícula do usuário", example = "REG-003")
        @NotBlank(message = "O registro é obrigatório")
        String registro,

        @Schema(description = "Perfil de acesso do usuário",
                example = "TECNICO",
                allowableValues = {"ADMIN", "ALMOXARIFE", "TECNICO"})
        PerfilUsuario perfil,

        @Schema(description = "Indica se o usuário está ativo no sistema", example = "true")
        Boolean ativo
) {}
