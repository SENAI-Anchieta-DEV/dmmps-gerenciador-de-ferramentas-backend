package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para cadastro ou atualização de um usuário")
public record UsuarioRequestDTO(

        @Schema(description = "Nome completo do usuário", example = "João Silva")
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @Schema(description = "E-mail de login do usuário (único)", example = "joao@toolhub.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "O e-mail deve ser válido")
        String email,

        @Schema(description = "Senha de acesso (será armazenada criptografada)", example = "senha123")
        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @Schema(description = "Registro/matrícula único do usuário na empresa", example = "REG-003")
        @NotBlank(message = "O registro é obrigatório")
        String registro,

        @Schema(description = "Perfil de acesso do usuário",
                example = "TECNICO",
                allowableValues = {"ADMIN", "ALMOXARIFE", "TECNICO"})
        PerfilUsuario perfil
) {}
