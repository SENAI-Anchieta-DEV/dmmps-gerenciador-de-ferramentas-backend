package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.LoginRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.TokenResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.AuthService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.config.OpenApiSchemas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@SecurityRequirements  // Remove o requisito JWT para este endpoint
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Operações de login e geração de token JWT")
public class AuthController {

        private final AuthService authService;

        @Operation(
                summary = "Realizar login",
                description = "Autentica o usuário com e-mail e senha e retorna um token JWT para uso nas demais rotas."
        )
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = TokenResponseDTO.class),
                                examples = @ExampleObject(value = """
                    { "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }
                """))),
                @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                                examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Credenciais Inválidas",
                      "detail": "E-mail ou senha incorretos.",
                      "instance": "/auth/login",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                                examples = @ExampleObject(value = """
                    {
                      "status": 500,
                      "title": "Erro interno do servidor",
                      "detail": "Ocorreu um erro inesperado. Contate o suporte.",
                      "instance": "/auth/login",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
        })
        @PostMapping("/login")
        @SecurityRequirements
        public ResponseEntity<TokenResponseDTO> login(
                @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Credenciais do usuário",
                        required = true,
                        content = @Content(examples = @ExampleObject(value = """
                    { "email": "admin@toolhub.com", "senha": "senha123" }
                """))
                )
                @RequestBody @Valid LoginRequestDTO dados) {
            return ResponseEntity.ok(authService.login(dados));
        }
    }
