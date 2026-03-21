package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DevolucaoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.EmprestimoService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/emprestimos")
@Tag(name = "Empréstimos", description = "Gestão de check-out e check-in de ferramentas (RF07, RF09, RF11, RF12, RF13)")
public class EmprestimosController {

    private final EmprestimoService emprestimoService;

    public EmprestimosController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @Operation(summary = "Realizar check-out", description = "Registra a retirada de uma ferramenta por um técnico. A ferramenta deve estar com status DISPONIVEL (RN01). Impede dois técnicos de pegar a mesma ferramenta simultaneamente (RF33).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Check-out realizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ferramenta ou usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Ferramenta indisponível para empréstimo", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<EmprestimoResponseDTO> realizarCheckOut(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                    {
                      "usuarioId": "uuid-do-tecnico",
                      "ferramentaId": "uuid-da-ferramenta"
                    }
                """))
            )
            @RequestBody EmprestimoRequestDTO dados) {
        EmprestimoResponseDTO response = emprestimoService.realizarCheckOut(dados);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Realizar check-in (devolução)", description = "Registra a devolução de uma ferramenta. O técnico informa o estado de conservação (RF12). Se DANIFICADA, a ferramenta vai para EM_MANUTENCAO automaticamente (RF13).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check-in realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Empréstimo já finalizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado", content = @Content)
    })
    @PatchMapping("/{id}/devolucao")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<EmprestimoResponseDTO> realizarCheckIn(@PathVariable UUID id,
                                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                         content = @Content(examples = @ExampleObject(value = """
                    { "estadoConservacao": "BOM_ESTADO" }
                """))
                                                                 )
                                                                 @RequestBody DevolucaoRequestDTO dados) {
        return ResponseEntity.ok(emprestimoService.realizarCheckIn(id, dados));
    }

    @Operation(summary = "Listar todos os empréstimos", description = "Retorna histórico completo de empréstimos. Acessível por ADMIN e ALMOXARIFE.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    @Operation(summary = "Buscar empréstimo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empréstimo encontrado"),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }

    @Operation(summary = "Histórico por técnico", description = "Lista todos os empréstimos de um técnico específico pelo seu ID.")
    @GetMapping("/tecnico/{idUsuario}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorTecnico(@PathVariable UUID idUsuario) {
        return ResponseEntity.ok(emprestimoService.listarPorTecnico(idUsuario));
    }

    @Operation(summary = "Histórico por ferramenta", description = "Lista todos os empréstimos associados a uma ferramenta específica.")
    @GetMapping("/ferramenta/{idFerramenta}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorFerramenta(@PathVariable UUID idFerramenta) {
        return ResponseEntity.ok(emprestimoService.listarPorFerramenta(idFerramenta));
    }

    @Operation(summary = "Meus empréstimos", description = "Lista os empréstimos do técnico autenticado (RF06). Usa o principal do JWT para identificar o usuário.")
    @GetMapping("/meus")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarMeus(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(emprestimoService.listarPorTecnico(usuarioAutenticado.getId()));
    }
}
