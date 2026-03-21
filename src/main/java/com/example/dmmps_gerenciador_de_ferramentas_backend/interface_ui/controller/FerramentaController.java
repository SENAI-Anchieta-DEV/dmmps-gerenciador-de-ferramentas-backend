package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.FerramentaRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.FerramentaResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.FerramentaService;
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
public class FerramentaController {
    private final FerramentaService ferramentaService;

    public FerramentaController(FerramentaService ferramentaService) {
        this.ferramentaService = ferramentaService;
    }

    // 1. LISTAR TODAS (GET) - Status 200 OK
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE', 'TECNICO')")
    public ResponseEntity<List<FerramentaResponseDTO>> listar() {
        List<FerramentaResponseDTO> ferramentas = ferramentaService.listarTodas();
        return ResponseEntity.ok(ferramentas);
    }

    // 2. BUSCAR POR ID (GET) - Status 200 OK
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE', 'TECNICO')")
    public ResponseEntity<FerramentaResponseDTO> buscarPorId(@PathVariable UUID id) {
        FerramentaResponseDTO ferramenta = ferramentaService.buscarPorId(id);
        return ResponseEntity.ok(ferramenta);
    }

    // 3. CADASTRAR (POST) - Status 201 Created
    // Retorna também o Header 'Location' com a URI do novo recurso
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<FerramentaResponseDTO> cadastrar(@RequestBody @Valid FerramentaRequestDTO dados) {
        FerramentaResponseDTO novaFerramenta = ferramentaService.cadastrar(dados);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaFerramenta.id())
                .toUri();

        return ResponseEntity.created(uri).body(novaFerramenta);
    }

    // 4. ATUALIZAR (PUT) - Status 200 OK
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<FerramentaResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody @Valid FerramentaRequestDTO dados) {

        FerramentaResponseDTO atualizada = ferramentaService.atualizar(id, dados);
        return ResponseEntity.ok(atualizada);
    }

    // 5. ATUALIZAR STATUS/PARCIAL (PATCH) - Status 200 OK
    // Ideal para integração com IoT (ex: mudar status para "EM_USO")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody @Valid String novoStatus) { // Pode ser um DTO específico depois

        try {
            // Remove aspas extras que o JSON pode enviar e converte para MAIÚSCULO
            String statusLimpo = novoStatus.replace("\"", "").trim().toUpperCase();

            // Tenta converter a String "EM_USO" para o Enum StatusFerramenta.EM_USO
            StatusFerramenta statusEnum = StatusFerramenta.valueOf(statusLimpo);

            ferramentaService.atualizarStatus(id, statusEnum);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            // Se mandarem um status que não existe (ex: "QUEBRADA"), retorna erro 400
            return ResponseEntity.badRequest().build();
        }
    }

    // 6. REMOVER (DELETE) - Status 204 No Content
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        ferramentaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
