package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.FerramentaRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.FerramentaResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.FerramentaService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.config.OpenApiSchemas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ferramentas")
@Tag(name = "Ferramentas", description = "CRUD de ferramentas e atualização de status via IoT")
public class FerramentaController {

    private final FerramentaService ferramentaService;

    public FerramentaController(FerramentaService ferramentaService) {
        this.ferramentaService = ferramentaService;
    }

    @Operation(summary = "Listar todas as ferramentas",
            description = "Retorna a lista completa de ferramentas cadastradas. Acessível por ADMIN, ALMOXARIFE e TECNICO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FerramentaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/ferramentas",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE', 'TECNICO')")
    public ResponseEntity<List<FerramentaResponseDTO>> listar() {
        return ResponseEntity.ok(ferramentaService.listarTodas());
    }

    @Operation(summary = "Buscar ferramenta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ferramenta encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FerramentaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/ferramentas/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Ferramenta não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Ferramenta não encontrada com id: 123e4567-e89b-12d3-a456-426614174000",
                      "instance": "/ferramentas/123e4567-e89b-12d3-a456-426614174000",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE', 'TECNICO')")
    public ResponseEntity<FerramentaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(ferramentaService.buscarPorId(id));
    }

    @Operation(summary = "Cadastrar ferramenta",
            description = "Cadastra uma nova ferramenta com status inicial DISPONIVEL. Código de patrimônio deve ser único.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ferramenta cadastrada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FerramentaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou código de patrimônio duplicado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ValidationProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "title": "Violação de regra de negócio",
                      "detail": "Já existe uma ferramenta cadastrada com o Código de Patrimônio PAT-001",
                      "instance": "/ferramentas",
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
                      "instance": "/ferramentas",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN ou ALMOXARIFE",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 403,
                      "title": "Acesso Negado",
                      "detail": "Você não tem permissão para acessar este recurso.",
                      "instance": "/ferramentas",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<FerramentaResponseDTO> cadastrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                    {
                      "nome": "Chave de Fenda Phillips",
                      "descricao": "Chave de fenda cabeça Phillips tamanho 2",
                      "fabricante": "Tramontina",
                      "codigoPatrimonio": "PAT-001",
                      "gavetaLocalizacao": "A3"
                    }
                """)))
            @RequestBody @Valid FerramentaRequestDTO dados) {
        FerramentaResponseDTO nova = ferramentaService.cadastrar(dados);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(nova.id()).toUri();
        return ResponseEntity.created(uri).body(nova);
    }

    @Operation(summary = "Atualizar ferramenta",
            description = "Atualiza os dados cadastrais de uma ferramenta existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ferramenta atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FerramentaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ValidationProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "title": "Erro de validação",
                      "detail": "Um ou mais campos estão inválidos",
                      "instance": "/ferramentas/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI",
                      "errors": { "nome": "O nome da ferramenta é obrigatório" }
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
                      "instance": "/ferramentas/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN ou ALMOXARIFE",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 403,
                      "title": "Acesso Negado",
                      "detail": "Você não tem permissão para acessar este recurso.",
                      "instance": "/ferramentas/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Ferramenta não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Ferramenta não encontrada com id: 123e4567-e89b-12d3-a456-426614174000",
                      "instance": "/ferramentas/123e4567-e89b-12d3-a456-426614174000",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<FerramentaResponseDTO> atualizar(@PathVariable UUID id,
                                                           @RequestBody @Valid FerramentaRequestDTO dados) {
        return ResponseEntity.ok(ferramentaService.atualizar(id, dados));
    }

    @Operation(summary = "Atualizar status da ferramenta",
            description = "Altera o status da ferramenta. Usado pela integração IoT/ESP32. Valores válidos: DISPONIVEL, EM_USO, EM_MANUTENCAO, DESCARTADA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "title": "Violação de regra de negócio",
                      "detail": "Status informado não é válido. Use: DISPONIVEL, EM_USO, EM_MANUTENCAO ou DESCARTADA.",
                      "instance": "/ferramentas/uuid/status",
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
                      "instance": "/ferramentas/uuid/status",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Ferramenta não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Ferramenta não encontrada com id: 123e4567-e89b-12d3-a456-426614174000",
                      "instance": "/ferramentas/123e4567-e89b-12d3-a456-426614174000/status",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<Void> atualizarStatus(@PathVariable UUID id,
                                                @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                        content = @Content(examples = @ExampleObject(value = "\"EM_MANUTENCAO\"")))
                                                @RequestBody @Valid String novoStatus) {
        try {
            String statusLimpo = novoStatus.replace("\"", "").trim().toUpperCase();
            ferramentaService.atualizarStatus(id, StatusFerramenta.valueOf(statusLimpo));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Remover ferramenta",
            description = "Remove permanentemente uma ferramenta do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ferramenta removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/ferramentas/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer ADMIN ou ALMOXARIFE",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 403,
                      "title": "Acesso Negado",
                      "detail": "Você não tem permissão para acessar este recurso.",
                      "instance": "/ferramentas/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Ferramenta não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Ferramenta não encontrada com id: 123e4567-e89b-12d3-a456-426614174000",
                      "instance": "/ferramentas/123e4567-e89b-12d3-a456-426614174000",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        ferramentaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
