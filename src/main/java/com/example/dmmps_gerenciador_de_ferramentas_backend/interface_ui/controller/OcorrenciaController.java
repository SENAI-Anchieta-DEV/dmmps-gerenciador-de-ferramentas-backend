package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.AtualizacaoStatusOcorrenciaDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.OcorrenciaRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.OcorrenciaResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.OcorrenciaService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.config.OpenApiSchemas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ocorrencias")
@Tag(name = "Ocorrências", description = "Registro e gestão de ocorrências de manutenção de ferramentas (RF14, RF15, RF16)")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    @Operation(summary = "Listar todas as ocorrências",
            description = "Retorna todas as ocorrências registradas. Acessível por ADMIN e ALMOXARIFE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OcorrenciaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/ocorrencias",
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
                      "instance": "/ocorrencias",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(ocorrenciaService.listarTodas());
    }

    // POST /ocorrencias — Abrir ocorrência (RF14)
    @Operation(summary = "Abrir ocorrência",
            description = "Registra uma ocorrência de problema em uma ferramenta. O usuário é identificado automaticamente pelo token JWT. A ferramenta é automaticamente movida para EM_MANUTENCAO (RF14).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ocorrência registrada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OcorrenciaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                {
                  "status": 401,
                  "title": "Não Autenticado",
                  "detail": "Autenticação ausente ou token inválido/expirado.",
                  "instance": "/ocorrencias",
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
                  "instance": "/ocorrencias",
                  "timestamp": "2025-03-21T14:30:00",
                  "application": "GerenciadorFerramentasAPI"
                }
            """)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('TECNICO', 'ALMOXARIFE')")
    public ResponseEntity<OcorrenciaResponseDTO> abrirOcorrencia(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "ferramentaId": "123e4567-e89b-12d3-a456-426614174000",
                  "titulo": "Chave com cabo quebrado",
                  "descricao": "O cabo da chave de fenda está rachado e pode causar acidentes."
                }
            """)))
            @RequestBody @Valid OcorrenciaRequestDTO dados,
            Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        OcorrenciaResponseDTO nova = ocorrenciaService.registrarOcorrencia(dados, usuarioAutenticado);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(nova.id()).toUri();
        return ResponseEntity.created(uri).body(nova);
    }

    @Operation(summary = "Buscar ocorrência por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ocorrência encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OcorrenciaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/ocorrencias/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Ocorrência não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Ocorrência não encontrada com id: 123e4567-e89b-12d3-a456-426614174000",
                      "instance": "/ocorrencias/123e4567-e89b-12d3-a456-426614174000",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<OcorrenciaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(ocorrenciaService.buscarPorId(id));
    }

    @Operation(summary = "Histórico de ocorrências por ferramenta",
            description = "Lista todas as ocorrências associadas a uma ferramenta (RF34).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OcorrenciaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/ocorrencias/ferramenta/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping("/ferramenta/{idFerramenta}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarPorFerramenta(@PathVariable UUID idFerramenta) {
        return ResponseEntity.ok(ocorrenciaService.listarPorFerramenta(idFerramenta));
    }

    @Operation(summary = "Atualizar status da ocorrência",
            description = """
            Atualiza o status de uma ocorrência (RF15, RF16, RN06):
            - **RESOLVIDA**: ferramenta retorna para DISPONIVEL automaticamente.
            - **DESCARTADA**: ferramenta vai para DESCARTADA. Justificativa é obrigatória (RN06).
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OcorrenciaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Justificativa ausente ao descartar",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "title": "Violação de regra de negócio",
                      "detail": "A justificativa é obrigatória ao descartar uma ocorrência",
                      "instance": "/ocorrencias/uuid/status",
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
                      "instance": "/ocorrencias/uuid/status",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer ALMOXARIFE",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 403,
                      "title": "Acesso Negado",
                      "detail": "Você não tem permissão para acessar este recurso.",
                      "instance": "/ocorrencias/uuid/status",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Ocorrência não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Ocorrência não encontrada com id: 123e4567-e89b-12d3-a456-426614174000",
                      "instance": "/ocorrencias/123e4567-e89b-12d3-a456-426614174000/status",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ALMOXARIFE')")
    public ResponseEntity<OcorrenciaResponseDTO> atualizarStatus(@PathVariable UUID id,
                                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                         content = @Content(examples = @ExampleObject(value = """
                    {
                      "statusOcorrencia": "DESCARTADA",
                      "justificativaDescarte": "Ferramenta sem conserto viável, custo supera o valor do equipamento."
                    }
                """)))
                                                                 @RequestBody @Valid AtualizacaoStatusOcorrenciaDTO dados) {
        return ResponseEntity.ok(ocorrenciaService.atualizarStatus(id, dados));
    }

    @Operation(summary = "Minhas ocorrências",
            description = "Lista as ocorrências abertas pelo técnico autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OcorrenciaResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/ocorrencias/minhas",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping("/minhas")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarMinhas(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(ocorrenciaService.listarPorUsuario(usuarioAutenticado.getId()));
    }
}
