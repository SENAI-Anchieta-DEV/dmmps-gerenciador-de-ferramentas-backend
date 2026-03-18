package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.LoginRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.TokenResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // Injeção do serviço agora está ativa (graças ao @RequiredArgsConstructor do Lombok)
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO dados) {
        // Agora delegamos a lógica real de autenticação para o AuthService
        TokenResponseDTO token = authService.login(dados);
        return ResponseEntity.ok(token);
    }
}