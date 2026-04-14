package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.UsuarioService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.config.OpenApiSchemas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuários", description = "Gestão de usuários do sistema. Restrito ao perfil ADMIN.")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar todos os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/api/v1/usuarios",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 403,
                      "title": "Acesso Negado",
                      "detail": "Você não tem permissão para acessar este recurso.",
                      "instance": "/api/v1/usuarios",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/api/v1/usuarios/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 403,
                      "title": "Acesso Negado",
                      "detail": "Você não tem permissão para acessar este recurso.",
                      "instance": "/api/v1/usuarios/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Usuário não encontrado com ID: 123e4567-e89b-12d3-a456-426614174000",
                      "instance": "/api/v1/usuarios/123e4567-e89b-12d3-a456-426614174000",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @Operation(summary = "Cadastrar usuário",
            description = "Cria um novo usuário no sistema. E-mail e registro devem ser únicos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "E-mail ou registro já cadastrado, ou dados inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ValidationProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "title": "Violação de regra de negócio",
                      "detail": "Já existe um usuário cadastrado com este e-mail.",
                      "instance": "/api/v1/usuarios",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/api/v1/usuarios",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 403,
                      "title": "Acesso Negado",
                      "detail": "Você não tem permissão para acessar este recurso.",
                      "instance": "/api/v1/usuarios",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                    {
                      "nome": "João Silva",
                      "email": "joao@toolhub.com",
                      "senha": "senha123",
                      "registro": "REG-003",
                      "perfil": "TECNICO"
                    }
                """)))
            @RequestBody @Valid UsuarioRequestDTO dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(dados));
    }

    @Operation(summary = "Atualizar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "E-mail já em uso ou dados inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ValidationProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "title": "Violação de regra de negócio",
                      "detail": "O e-mail informado já está em uso.",
                      "instance": "/api/v1/usuarios/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/api/v1/usuarios/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 403,
                      "title": "Acesso Negado",
                      "detail": "Você não tem permissão para acessar este recurso.",
                      "instance": "/api/v1/usuarios/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Usuário não encontrado com ID: 123e4567-e89b-12d3-a456-426614174000",
                      "instance": "/api/v1/usuarios/123e4567-e89b-12d3-a456-426614174000",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable UUID id,
                                                        @RequestBody @Valid UsuarioRequestDTO dados) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dados));
    }

    @Operation(summary = "Inativar usuário",
            description = "Realiza exclusão lógica do usuário (campo ativo = false). O registro é mantido para trilha de auditoria.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário inativado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/api/v1/usuarios/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 403,
                      "title": "Acesso Negado",
                      "detail": "Você não tem permissão para acessar este recurso.",
                      "instance": "/api/v1/usuarios/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Usuário não encontrado com ID: 123e4567-e89b-12d3-a456-426614174000",
                      "instance": "/api/v1/usuarios/123e4567-e89b-12d3-a456-426614174000",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}