package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DispositivoIoTRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DispositivoIoTResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.DispositivoIoTService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusConectividade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/dispositivos")
@Tag(name = "Dispositivos IoT", description = "Gerenciamento de dispositivos IoT cadastrados no sistema (RF28, RF29)")
public class DispositivoIoTController {

    private final DispositivoIoTService dispositivoService;

    public DispositivoIoTController(DispositivoIoTService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @Operation(summary = "Cadastrar dispositivo IoT", description = "Registra um novo dispositivo IoT pelo MAC address. O MAC deve ser único no sistema. (RF28, RN04)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dispositivo cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "MAC address inválido ou dados ausentes"),
            @ApiResponse(responseCode = "409", description = "MAC address já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<DispositivoIoTResponseDTO> cadastrar(@RequestBody @Valid DispositivoIoTRequestDTO dados) {
        DispositivoIoTResponseDTO response = dispositivoService.cadastrar(dados);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Listar dispositivos IoT", description = "Retorna todos os dispositivos IoT cadastrados no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<DispositivoIoTResponseDTO>> listarTodos() {
        return ResponseEntity.ok(dispositivoService.listarTodos());
    }

    @Operation(summary = "Buscar dispositivo por ID", description = "Retorna os dados de um dispositivo IoT específico pelo seu UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dispositivo encontrado"),
            @ApiResponse(responseCode = "404", description = "Dispositivo não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<DispositivoIoTResponseDTO> buscarPorId(
            @Parameter(description = "UUID do dispositivo IoT") @PathVariable UUID id) {
        return ResponseEntity.ok(dispositivoService.buscarPorId(id));
    }

    @Operation(summary = "Atualizar dispositivo IoT", description = "Atualiza os dados de um dispositivo IoT existente. O novo MAC address deve ser único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dispositivo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Dispositivo não encontrado"),
            @ApiResponse(responseCode = "409", description = "MAC address já em uso por outro dispositivo"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<DispositivoIoTResponseDTO> atualizar(
            @Parameter(description = "UUID do dispositivo IoT") @PathVariable UUID id,
            @RequestBody @Valid DispositivoIoTRequestDTO dados) {
        return ResponseEntity.ok(dispositivoService.atualizar(id, dados));
    }

    @Operation(summary = "Remover dispositivo IoT", description = "Remove um dispositivo IoT do sistema pelo seu UUID. (RN04)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dispositivo removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Dispositivo não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> remover(
            @Parameter(description = "UUID do dispositivo IoT") @PathVariable UUID id) {
        dispositivoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Consultar status de conectividade", description = "Retorna o status de conectividade atual do dispositivo IoT, atualizado via MQTT. (RF29)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Dispositivo não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<StatusConectividade> consultarStatus(
            @Parameter(description = "UUID do dispositivo IoT") @PathVariable UUID id) {
        return ResponseEntity.ok(dispositivoService.consultarStatus(id));
    }
}