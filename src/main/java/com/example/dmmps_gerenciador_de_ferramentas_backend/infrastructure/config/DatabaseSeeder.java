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
        // Tenta encontrar o Marcus (Admin) pelo e-mail
        usuarioRepository.findByEmail("admin@toolhub.com").ifPresentOrElse(
                usuario -> {
                    // Força a atualização da senha para o formato criptografado correto
                    usuario.setSenha(passwordEncoder.encode("senha123"));
                    usuarioRepository.save(usuario);
                    System.out.println("🔄 Senha do admin@toolhub.com atualizada com sucesso!");
                },
                () -> {
                    // Se não existir, cria o novo
                    Usuario admin = new Usuario(
                            "Marcus (Admin)",
                            "admin@toolhub.com",
                            passwordEncoder.encode("senha123"),
                            "REG-001",
                            PerfilUsuario.ADMIN
                    );
                    usuarioRepository.save(admin);
                    System.out.println("✅ Novo usuário ADMIN criado com sucesso!");
                }
        );
    }
}