package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.OcorrenciaRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.OcorrenciaResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {
    // 1. LISTAR TODAS (Para o Almoxarife/Admin verem o painel de problemas)
    @GetMapping
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(List.of());
    }

    // 2. ABRIR OCORRÊNCIA (Para o Técnico relatar o dano na devolução)
    @PostMapping
    public ResponseEntity<OcorrenciaResponseDTO> abrirOcorrencia(@RequestBody OcorrenciaRequestDTO dados) {
        UUID fakeId = UUID.randomUUID();
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(fakeId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    // 3. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<OcorrenciaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.notFound().build();
    }
}
