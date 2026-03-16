package com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.security;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.TokenService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extrairToken(request);

        if (token != null) {
            String email = tokenService.validarToken(token);

            if (email != null) {
                var usuarioOpt = usuarioService.buscarPorEmail(email);

                if (usuarioOpt.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                var usuario = usuarioOpt.get();

                var auth = new UsernamePasswordAuthenticationToken(
                        usuario, null, usuario.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extrairToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        return header.replace("Bearer ", "");
    }
}