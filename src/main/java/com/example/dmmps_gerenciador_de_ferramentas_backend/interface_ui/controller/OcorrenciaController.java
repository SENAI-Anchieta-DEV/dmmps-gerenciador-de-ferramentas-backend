package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.AtualizacaoStatusOcorrenciaDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.OcorrenciaRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.OcorrenciaResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.OcorrenciaService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
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
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    // 1. LISTAR TODAS (GET) - Status 200 OK
    // Restrito a ALMOXARIFE e GESTOR
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarTodas() {
        List<OcorrenciaResponseDTO> ocorrencias = ocorrenciaService.listarTodas();
        return ResponseEntity.ok(ocorrencias);
    }

    // 2. ABRIR OCORRÊNCIA (POST) - Status 201 Created
    // Restrito a TECNICO e ALMOXARIFE (RF14)
    @PostMapping
    @PreAuthorize("hasAnyRole('TECNICO', 'ALMOXARIFE')")
    public ResponseEntity<OcorrenciaResponseDTO> abrirOcorrencia(@RequestBody @Valid OcorrenciaRequestDTO dados) {
        OcorrenciaResponseDTO novaOcorrencia = ocorrenciaService.registrarOcorrencia(dados);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaOcorrencia.id())
                .toUri();

        return ResponseEntity.created(uri).body(novaOcorrencia);
    }

    // 3. BUSCAR POR ID (GET) - Status 200 OK
    // Restrito a ALMOXARIFE e GESTOR
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<OcorrenciaResponseDTO> buscarPorId(@PathVariable UUID id) {
        OcorrenciaResponseDTO ocorrencia = ocorrenciaService.buscarPorId(id);
        return ResponseEntity.ok(ocorrencia);
    }

    // 4. HISTÓRICO POR FERRAMENTA (GET) - Status 200 OK
    // Restrito a ALMOXARIFE e GESTOR (RF34)
    @GetMapping("/ferramenta/{idFerramenta}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMOXARIFE')")
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarPorFerramenta(@PathVariable UUID idFerramenta) {
        List<OcorrenciaResponseDTO> ocorrencias = ocorrenciaService.listarPorFerramenta(idFerramenta);
        return ResponseEntity.ok(ocorrencias);
    }

    // 5. ATUALIZAR STATUS (PATCH) - Status 200 OK
    // Restrito a ALMOXARIFE (RF15, RF16, RN06)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ALMOXARIFE')")
    public ResponseEntity<OcorrenciaResponseDTO> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody @Valid AtualizacaoStatusOcorrenciaDTO dados) {

        OcorrenciaResponseDTO atualizada = ocorrenciaService.atualizarStatus(id, dados);
        return ResponseEntity.ok(atualizada);
    }

    // GET /ocorrencias/minhas — Ocorrências do técnico autenticado
    @GetMapping("/minhas")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarMinhas(Authentication authentication) {
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok(ocorrenciaService.listarPorUsuario(usuarioAutenticado.getId()));
    }
}
