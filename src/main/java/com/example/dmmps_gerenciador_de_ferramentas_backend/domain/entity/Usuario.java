package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    // A documentação cita "Registro/ID" ou "matrícula"
    @Column(unique = true, nullable = false)
    private String registro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil;

    @Column(nullable = false)
    private Boolean ativo;

    public Usuario(String nome, String email, String senha, String registro, PerfilUsuario perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.registro = registro;
        this.perfil = perfil;
        this.ativo = true;
    }

    // =========================================================
    // MÉTODOS DA INTERFACE USERDETAILS (SPRING SECURITY)
    // =========================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mapeia o Enum de Perfil para o formato de Roles do Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.perfil.name()));
    }

    @Override
    public String getPassword() {
        // Retorna a senha criptografada salva no banco
        return this.senha;
    }

    @Override
    public String getUsername() {
        // Define qual campo será a "chave" de login (neste caso, o email)
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Define que a conta não expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Define que a conta não bloqueia
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Define que as credenciais (senha) não expiram
    }

    @Override
    public boolean isEnabled() {
        // Usa o seu campo "ativo" para dizer ao Spring se o usuário pode logar
        return this.ativo != null ? this.ativo : false;
    }
}