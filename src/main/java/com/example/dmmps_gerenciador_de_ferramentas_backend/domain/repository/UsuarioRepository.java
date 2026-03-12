package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    boolean existsByEmail(String email);

    boolean existsByRegistro(String registro);

    Optional<Usuario> findByEmail(String email);
}