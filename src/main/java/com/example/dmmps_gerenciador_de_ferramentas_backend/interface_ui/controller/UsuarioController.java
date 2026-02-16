package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    // Injete o Service futuramente
    // private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        // TODO: Implementar lógica
        return ResponseEntity.ok(List.of());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody UsuarioRequestDTO dados) {
        // TODO: Implementar lógica
        return ResponseEntity.status(201).build(); // Mock temporário
    }

    // Adicione GET /{id}, PUT, DELETE conforme feito em Ferramentas
}
