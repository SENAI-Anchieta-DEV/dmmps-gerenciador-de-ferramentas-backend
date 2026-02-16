package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimosController {
    @PostMapping("/retirada") // Ação de pegar ferramenta
    public ResponseEntity<Void> registrarRetirada(@RequestBody Object dadosRetirada) {
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/devolucao/{id}") // Ação de devolver
    public ResponseEntity<Void> registrarDevolucao(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }
}
