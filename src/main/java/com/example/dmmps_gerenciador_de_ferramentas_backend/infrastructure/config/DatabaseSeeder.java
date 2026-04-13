package com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.config;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.PerfilUsuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Gerenciar Usuário ADMIN
        seedUsuario(
                "Marcus (Admin)",
                "admin@toolhub.com",
                "senha123",
                "REG-001",
                PerfilUsuario.ADMIN
        );

        // 2. Gerenciar Usuário TÉCNICO
        seedUsuario(
                "Técnico Silva",
                "tecnico@toolhub.com",
                "senha123",
                "REG-002",
                PerfilUsuario.TECNICO
        );
    }

    private void seedUsuario(String nome, String email, String senhaPura, String registro, PerfilUsuario perfil) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            // Se não existe, cria do zero
            Usuario novoUsuario = new Usuario(
                    nome,
                    email,
                    passwordEncoder.encode(senhaPura),
                    registro,
                    perfil
            );
            usuarioRepository.save(novoUsuario);
            System.out.println("✅ Usuário criado: " + email);
        } else {
            // Se já existe, garante que a senha está criptografada (BCrypt)
            Usuario usuarioExistente = usuarioOpt.get();
            usuarioExistente.setSenha(passwordEncoder.encode(senhaPura));
            usuarioRepository.save(usuarioExistente);
            System.out.println("🔄 Senha atualizada para: " + email);
        }
    }
}