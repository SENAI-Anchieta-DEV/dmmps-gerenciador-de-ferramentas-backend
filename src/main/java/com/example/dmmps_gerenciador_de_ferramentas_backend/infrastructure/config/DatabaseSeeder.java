package com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.config;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.PerfilUsuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o banco já tem usuários para não duplicar caso você mude para PostgreSQL no futuro
        if (usuarioRepository.count() == 0) {

            // 1. Cria um usuário ADMIN
            Usuario admin = new Usuario(
                    "Marcus (Admin)",
                    "admin@toolhub.com",
                    passwordEncoder.encode("senha123"), // Criptografa a senha na hora de salvar
                    "REG-001",
                    PerfilUsuario.ADMIN
            );

            // 2. Cria um usuário TÉCNICO
            Usuario tecnico = new Usuario(
                    "Técnico Silva",
                    "tecnico@toolhub.com",
                    passwordEncoder.encode("senha123"),
                    "REG-002",
                    PerfilUsuario.TECNICO
            );

            usuarioRepository.save(admin);
            usuarioRepository.save(tecnico);

            System.out.println("✅ Usuários de teste criados no banco H2 com sucesso!");
        }
    }
}