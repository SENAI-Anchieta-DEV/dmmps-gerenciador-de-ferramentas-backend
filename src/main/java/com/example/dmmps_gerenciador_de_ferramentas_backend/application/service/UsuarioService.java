package com.example.dmmps_gerenciador_de_ferramentas_backend.application.service;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.EmailJaCadastradoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.NegocioException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.UsuarioNaoEncontradoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        // 1. Validações de regra de negócio
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException("Já existe um usuário cadastrado com este e-mail.");
        }
        if (usuarioRepository.existsByRegistro(dto.registro())) {
            throw new NegocioException("Já existe um usuário com este registro/matrícula.");
        }

        // 2. Criação da entidade e criptografia da senha
        Usuario novoUsuario = new Usuario(
                dto.nome(),
                dto.email(),
                passwordEncoder.encode(dto.senha()), // Criptografa a senha antes de salvar!
                dto.registro(),
                dto.perfil()
        );

        // 3. Salva no banco de dados
        usuarioRepository.save(novoUsuario);

        // 4. Retorna o DTO de resposta (sem a senha, por segurança)
        return new UsuarioResponseDTO(
                novoUsuario.getId(),
                novoUsuario.getNome(),
                novoUsuario.getEmail(),
                novoUsuario.getRegistro(),
                novoUsuario.getPerfil(),
                novoUsuario.getAtivo()
        );
    }

    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioRequestDTO dados) {
        Usuario usuario = buscarEntidadePorId(id);

        if (!usuario.getEmail().equals(dados.email()) && usuarioRepository.existsByEmail(dados.email())) {
            throw new EmailJaCadastradoException("O e-mail informado já está em uso.");
        }

        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setRegistro(dados.registro());
        usuario.setPerfil(dados.perfil());

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void inativar(UUID id) {
        Usuario usuario = buscarEntidadePorId(id);


        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(UUID id) {
        return toResponseDTO(buscarEntidadePorId(id));
    }

    private Usuario buscarEntidadePorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.getRegistro(), u.getPerfil(), u.getAtivo());
    }
}