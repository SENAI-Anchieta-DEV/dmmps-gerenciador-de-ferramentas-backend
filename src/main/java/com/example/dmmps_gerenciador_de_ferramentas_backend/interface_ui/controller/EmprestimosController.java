package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimosController {
    // 1. LISTAR HISTÓRICO (GET)
    @GetMapping
    public ResponseEntity<List<EmprestimoResponseDTO>> listarHistorico() {
        return ResponseEntity.ok(List.of());
    }

    // 2. CONSULTAR EMPRÉSTIMO ESPECÍFICO (GET)
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.notFound().build();
    }

    // 3. REGISTRAR RETIRADA (POST) - Cria um novo empréstimo
    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> registrarRetirada(@RequestBody @Valid EmprestimoRequestDTO dados) {
        // Gera um ID fictício para validar o contrato REST 201 Created
        UUID fakeId = UUID.randomUUID();

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(fakeId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    // 4. REGISTRAR DEVOLUÇÃO (PATCH)
    // Usamos PATCH porque estamos a finalizar um recurso aberto, atualizando a data de devolução
    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<Void> registrarDevolucao(@PathVariable UUID id) {
        // Futuramente: Service.finalizarEmprestimo(id);
        return ResponseEntity.noContent().build();
    }
}
