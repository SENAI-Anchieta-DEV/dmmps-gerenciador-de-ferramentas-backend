package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    // 1. LISTAR TODOS (GET)
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(List.of());
    }

    // 2. BUSCAR POR ID (GET) - Falta implementar lógica
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        // Retornando 404 temporariamente para mostrar que o endpoint existe
        return ResponseEntity.notFound().build();
    }

    // 3. CADASTRAR (POST)
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody UsuarioRequestDTO dados) {
        // Mock de ID para o URI funcionar
        UUID fakeId = UUID.randomUUID();

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(fakeId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    // 4. ATUALIZAR COMPLETO (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody UsuarioRequestDTO dados) {

        return ResponseEntity.ok().build();
    }

    // 5. INATIVAR/ATIVAR (PATCH) - Muito útil para Usuários
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody Boolean ativo) {

        return ResponseEntity.ok().build();
    }

    // 6. REMOVER (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        return ResponseEntity.noContent().build();
    }
}