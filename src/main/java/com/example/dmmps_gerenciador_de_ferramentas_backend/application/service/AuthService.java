package com.example.dmmps_gerenciador_de_ferramentas_backend.application.service;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.LoginRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.TokenResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.UsuarioRepository;
import com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public TokenResponseDTO login(LoginRequestDTO request) {
        // Valida as credenciais utilizando o Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        // Busca o utilizador autenticado para gerar o token
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado após autenticação"));

        // Gera o JWT real
        String jwtToken = jwtService.generateToken(usuario);

        return new TokenResponseDTO(jwtToken);
    }
}