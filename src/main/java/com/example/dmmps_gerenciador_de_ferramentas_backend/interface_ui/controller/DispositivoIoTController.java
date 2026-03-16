package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DispositivoIoTRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DispositivoIoTResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.DispositivoIoTService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusConectividade;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dispositivos")
public class DispositivoIoTController {

    private final DispositivoIoTService dispositivoService;

    public DispositivoIoTController(DispositivoIoTService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    // POST /dispositivos — Cadastrar (RF28, RN04)
    @PostMapping
    public ResponseEntity<DispositivoIoTResponseDTO> cadastrar(@RequestBody @Valid DispositivoIoTRequestDTO dados) {
        DispositivoIoTResponseDTO response = dispositivoService.cadastrar(dados);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    // GET /dispositivos — Listar todos
    @GetMapping
    public ResponseEntity<List<DispositivoIoTResponseDTO>> listarTodos() {
        return ResponseEntity.ok(dispositivoService.listarTodos());
    }

    // GET /dispositivos/{id} — Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<DispositivoIoTResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(dispositivoService.buscarPorId(id));
    }

    // PUT /dispositivos/{id} — Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<DispositivoIoTResponseDTO> atualizar(@PathVariable UUID id,
                                                               @RequestBody @Valid DispositivoIoTRequestDTO dados) {
        return ResponseEntity.ok(dispositivoService.atualizar(id, dados));
    }

    // DELETE /dispositivos/{id} — Remover (RN04)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        dispositivoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    // GET /dispositivos/{id}/status — Consultar status MQTT (RF29)
    @GetMapping("/{id}/status")
    public ResponseEntity<StatusConectividade> consultarStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(dispositivoService.consultarStatus(id));
    }
}
