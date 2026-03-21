package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.FerramentaRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.FerramentaResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.FerramentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Listar todas as ferramentas", description = "Retorna a lista completa de ferramentas cadastradas. Acessível por ADMIN, ALMOXARIFE e TECNICO.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE', 'TECNICO')")
    public ResponseEntity<List<FerramentaResponseDTO>> listar() {
        return ResponseEntity.ok(ferramentaService.listarTodas());
    }

    @Operation(summary = "Buscar ferramenta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ferramenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Ferramenta não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE', 'TECNICO')")
    public ResponseEntity<FerramentaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(ferramentaService.buscarPorId(id));
    }

    @Operation(summary = "Cadastrar ferramenta", description = "Cadastra uma nova ferramenta com status inicial DISPONIVEL. Código de patrimônio deve ser único.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ferramenta cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Código de patrimônio duplicado ou dados inválidos", content = @Content)
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
                """))
            )
            @RequestBody @Valid FerramentaRequestDTO dados) {
        FerramentaResponseDTO nova = ferramentaService.cadastrar(dados);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(nova.id()).toUri();
        return ResponseEntity.created(uri).body(nova);
    }

    @Operation(summary = "Atualizar ferramenta", description = "Atualiza os dados cadastrais de uma ferramenta existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ferramenta atualizada"),
            @ApiResponse(responseCode = "404", description = "Ferramenta não encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<FerramentaResponseDTO> atualizar(@PathVariable UUID id,
                                                           @RequestBody @Valid FerramentaRequestDTO dados) {
        return ResponseEntity.ok(ferramentaService.atualizar(id, dados));
    }

    @Operation(summary = "Atualizar status da ferramenta", description = "Altera o status da ferramenta. Usado pela integração IoT/ESP32. Valores válidos: DISPONIVEL, EM_USO, EM_MANUTENCAO, DESCARTADA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado"),
            @ApiResponse(responseCode = "400", description = "Status inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ferramenta não encontrada", content = @Content)
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<Void> atualizarStatus(@PathVariable UUID id,
                                                @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                        content = @Content(examples = @ExampleObject(value = "\"EM_MANUTENCAO\""))
                                                )
                                                @RequestBody @Valid String novoStatus) {
        try {
            String statusLimpo = novoStatus.replace("\"", "").trim().toUpperCase();
            ferramentaService.atualizarStatus(id, StatusFerramenta.valueOf(statusLimpo));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Remover ferramenta", description = "Remove permanentemente uma ferramenta do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ferramenta removida"),
            @ApiResponse(responseCode = "404", description = "Ferramenta não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        ferramentaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
