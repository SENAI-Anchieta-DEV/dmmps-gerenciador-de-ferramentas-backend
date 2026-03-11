package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DevolucaoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.EmprestimoService;
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

    private final EmprestimoService emprestimoService;

    public EmprestimosController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    // POST /emprestimos — Check-out (RF07, RF09)
    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> realizarCheckOut(@RequestBody EmprestimoRequestDTO dados) {
        EmprestimoResponseDTO response = emprestimoService.realizarCheckOut(dados);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    // PATCH /emprestimos/{id}/devolucao — Check-in (RF11, RF12, RF13)
    @PatchMapping("/{id}/devolucao")
    public ResponseEntity<EmprestimoResponseDTO> realizarCheckIn(@PathVariable UUID id,
                                                                 @RequestBody DevolucaoRequestDTO dados) {
        return ResponseEntity.ok(emprestimoService.realizarCheckIn(id, dados));
    }

    // GET /emprestimos — Listar todos
    @GetMapping
    public ResponseEntity<List<EmprestimoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    // GET /emprestimos/{id} — Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }

    // GET /emprestimos/tecnico/{idUsuario} — Histórico por técnico
    @GetMapping("/tecnico/{idUsuario}")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorTecnico(@PathVariable UUID idUsuario) {
        return ResponseEntity.ok(emprestimoService.listarPorTecnico(idUsuario));
    }

    // GET /emprestimos/ferramenta/{idFerramenta} — Histórico por ferramenta
    @GetMapping("/ferramenta/{idFerramenta}")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarPorFerramenta(@PathVariable UUID idFerramenta) {
        return ResponseEntity.ok(emprestimoService.listarPorFerramenta(idFerramenta));
    }
}
