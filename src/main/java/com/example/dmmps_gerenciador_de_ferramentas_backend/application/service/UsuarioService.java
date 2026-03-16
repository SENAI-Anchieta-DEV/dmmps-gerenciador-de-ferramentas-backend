package com.example.dmmps_gerenciador_de_ferramentas_backend.application.service;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.UsuarioResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.EmailJaCadastradoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.NegocioException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.UsuarioNaoEncontradoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dados) {
        if (usuarioRepository.existsByEmail(dados.email())) {
            throw new EmailJaCadastradoException("Já existe um usuário com o e-mail: " + dados.email());
        }
        if (usuarioRepository.existsByRegistro(dados.registro())) {
            throw new NegocioException("Já existe um usuário com este registro/matrícula.");
        }

        Usuario novoUsuario = new Usuario(
                dados.nome(),
                dados.email(),
                dados.senha(),
                dados.registro(),
                dados.perfil()
        );

        return toResponseDTO(usuarioRepository.save(novoUsuario));
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

    // novo metodo — usado pelo SecurityFilter
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    private Usuario buscarEntidadePorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.getRegistro(), u.getPerfil(), u.getAtivo());
    }
}