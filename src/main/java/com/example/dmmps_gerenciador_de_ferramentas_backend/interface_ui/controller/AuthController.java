package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.LoginRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.TokenResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    /*private final AuthService auth;*/

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO dados) {
        // A lógica real virá na Task 2.7 com Spring Security
        return ResponseEntity.ok(new TokenResponseDTO("token_dummy_por_enquanto"));
    }
}
