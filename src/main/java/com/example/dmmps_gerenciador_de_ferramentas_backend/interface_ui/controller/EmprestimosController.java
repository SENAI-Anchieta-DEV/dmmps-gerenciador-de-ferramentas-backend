package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DevolucaoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.EmprestimoService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/emprestimos")
@Tag(name = "Empréstimos", description = "Gestão de check-out e check-in de ferramentas (RF07, RF09, RF11, RF12, RF13)")
public class EmprestimosController {

    private final EmprestimoService emprestimoService;

    public EmprestimosController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @Operation(summary = "Realizar check-out",
            description = "Registra a retirada de uma ferramenta. O técnico é identificado automaticamente pelo token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Check-out realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                {
                  "status": 401,
                  "title": "Não Autenticado",
                  "detail": "Autenticação ausente ou token inválido/expirado.",
                  "instance": "/emprestimos",
                  "timestamp": "2025-03-21T14:30:00",
                  "application": "GerenciadorFerramentasAPI"
                }
            """))),
            @ApiResponse(responseCode = "403", description = "Sem permissão — requer TECNICO",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                {
                  "status": 403,
                  "title": "Acesso Negado",
                  "detail": "Você não tem permissão para acessar este recurso.",
                  "instance": "/emprestimos",
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
                  "instance": "/emprestimos",
                  "timestamp": "2025-03-21T14:30:00",
                  "application": "GerenciadorFerramentasAPI"
                }
            """))),
            @ApiResponse(responseCode = "409", description = "Ferramenta indisponível para empréstimo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                {
                  "status": 409,
                  "title": "Ferramenta indisponível",
                  "detail": "A ferramenta não está disponível para empréstimo. Motivo: Status atual: EM_USO",
                  "instance": "/emprestimos",
                  "timestamp": "2025-03-21T14:30:00",
                  "application": "GerenciadorFerramentasAPI"
                }
            """)))
    })
    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<EmprestimoResponseDTO> realizarCheckOut(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                {
                  "ferramentaId": "987fcdeb-51a2-43f7-b210-111122223333"
                }
            """)))
            @RequestBody EmprestimoRequestDTO dados,
            Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        EmprestimoResponseDTO response = emprestimoService.realizarCheckOut(dados, usuarioAutenticado);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Realizar check-in (devolução)",
            description = "Registra a devolução de uma ferramenta. O técnico informa o estado de conservação (RF12). Se DANIFICADA, a ferramenta vai para EM_MANUTENCAO automaticamente (RF13).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check-in realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Empréstimo já finalizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 400,
                      "title": "Violação de regra de negócio",
                      "detail": "Empréstimo com id 123e4567-e89b-12d3-a456-426614174000 já foi finalizado.",
                      "instance": "/emprestimos/uuid/devolucao",
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
                      "instance": "/emprestimos/uuid/devolucao",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Empréstimo com id 123e4567-e89b-12d3-a456-426614174000 não encontrado.",
                      "instance": "/emprestimos/123e4567-e89b-12d3-a456-426614174000/devolucao",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @PatchMapping("/{id}/devolucao")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<EmprestimoResponseDTO> realizarCheckIn(@PathVariable UUID id,
                                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                         content = @Content(examples = @ExampleObject(value = """
                    { "estadoConservacao": "BOM_ESTADO" }
                """)))
                                                                 @RequestBody DevolucaoRequestDTO dados) {
        return ResponseEntity.ok(emprestimoService.realizarCheckIn(id, dados));
    }

    @Operation(summary = "Listar todos os empréstimos",
            description = "Retorna histórico completo de empréstimos. Acessível por ADMIN e ALMOXARIFE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/emprestimos",
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
                      "instance": "/emprestimos",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    @Operation(summary = "Buscar empréstimo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empréstimo encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/emprestimos/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 404,
                      "title": "Recurso não encontrado",
                      "detail": "Empréstimo com id 123e4567-e89b-12d3-a456-426614174000 não encontrado.",
                      "instance": "/emprestimos/123e4567-e89b-12d3-a456-426614174000",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }

    @Operation(summary = "Histórico por técnico",
            description = "Lista todos os empréstimos de um técnico específico pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/emprestimos/tecnico/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping("/tecnico/{idUsuario}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorTecnico(@PathVariable UUID idUsuario) {
        return ResponseEntity.ok(emprestimoService.listarPorTecnico(idUsuario));
    }

    @Operation(summary = "Histórico por ferramenta",
            description = "Lista todos os empréstimos associados a uma ferramenta específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/emprestimos/ferramenta/uuid",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping("/ferramenta/{idFerramenta}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorFerramenta(@PathVariable UUID idFerramenta) {
        return ResponseEntity.ok(emprestimoService.listarPorFerramenta(idFerramenta));
    }

    @Operation(summary = "Meus empréstimos",
            description = "Lista os empréstimos do técnico autenticado (RF06). Usa o principal do JWT para identificar o usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmprestimoResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenApiSchemas.ProblemDetailSchema.class),
                            examples = @ExampleObject(value = """
                    {
                      "status": 401,
                      "title": "Não Autenticado",
                      "detail": "Autenticação ausente ou token inválido/expirado.",
                      "instance": "/emprestimos/meus",
                      "timestamp": "2025-03-21T14:30:00",
                      "application": "GerenciadorFerramentasAPI"
                    }
                """)))
    })
    @GetMapping("/meus")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarMeus(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(emprestimoService.listarPorTecnico(usuarioAutenticado.getId()));
    }

    @PostMapping("/solicitar/{ferramentaId}")
    public ResponseEntity<EmprestimoResponseDTO> solicitarFerramenta(@PathVariable UUID ferramentaId) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmprestimoResponseDTO response = emprestimoService.solicitarFerramenta(ferramentaId, usuarioLogado);
        return ResponseEntity.ok(response);
    }
}
