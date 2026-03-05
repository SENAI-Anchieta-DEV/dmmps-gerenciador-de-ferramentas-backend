package com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.security;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorizationService implements UserDetailsService {
    private final UsuarioRepository repository;
    public AuthorizationService(UsuarioRepository repository) { this.repository = repository; }

    @Override
    public Optional<Usuario> loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username);


    }
}