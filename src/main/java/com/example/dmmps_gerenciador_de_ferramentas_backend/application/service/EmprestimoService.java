package com.example.dmmps_gerenciador_de_ferramentas_backend.application.service;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DevolucaoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Emprestimo;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Ferramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.EstadoConservacao;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusEmprestimo;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.*;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.EmprestimoRepository;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.FerramentaRepository;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final FerramentaRepository ferramentaRepository;
    private final UsuarioRepository usuarioRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             FerramentaRepository ferramentaRepository,
                             UsuarioRepository usuarioRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.ferramentaRepository = ferramentaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // --- 1. CHECK-OUT (RF07, RF09, RN01, RF33) ---
    @Transactional
    public EmprestimoResponseDTO realizarCheckOut(EmprestimoRequestDTO dados) {
        Ferramenta ferramenta = ferramentaRepository.findById(dados.ferramentaId())
                .orElseThrow(() -> new FerramentaNaoEncontradaException(dados.ferramentaId()));

        // RN01 — Impede check-out se ferramenta não estiver DISPONIVEL
        if (ferramenta.getStatus() != StatusFerramenta.DISPONIVEL) {
            throw new FerramentaIndisponivelException(dados.ferramentaId(), "Status atual: " + ferramenta.getStatus().name());
        }

        // RF33 — Impede dois técnicos de pegar a mesma ferramenta simultaneamente
        emprestimoRepository.findByFerramentaIdAndStatusEmprestimo(dados.ferramentaId(), StatusEmprestimo.ABERTO)
                .ifPresent(e -> { throw new FerramentaIndisponivelException(dados.ferramentaId(), "Já existe um empréstimo em aberto para esta ferramenta."); });

        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(dados.usuarioId().toString()));

        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setFerramenta(ferramenta);
        novoEmprestimo.setUsuario(usuario);
        novoEmprestimo.setStatusEmprestimo(StatusEmprestimo.ABERTO);

        // Atualiza status da ferramenta para EM_USO automaticamente
        ferramenta.setStatus(StatusFerramenta.EM_USO);
        ferramentaRepository.save(ferramenta);

        Emprestimo salvo = emprestimoRepository.save(novoEmprestimo);
        return toResponseDTO(salvo);
    }

    // --- 2. CHECK-IN / DEVOLUÇÃO (RF11, RF12, RF13, RN03) ---
    @Transactional
    public EmprestimoResponseDTO realizarCheckIn(UUID id, DevolucaoRequestDTO dados) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new EmprestimoNaoEncontradoException(
                        "Empréstimo com id " + id + " não encontrado."));

        // Impede finalizar empréstimo já finalizado
        if (emprestimo.getStatusEmprestimo() == StatusEmprestimo.FINALIZADO) {
            throw new EmprestimoJaFinalizadoException("Empréstimo com id " + id + " já foi finalizado.");
        }

        emprestimo.setEstadoConservacao(dados.estadoConservacao());
        emprestimo.setDataDevolucao(LocalDateTime.now());
        emprestimo.setStatusEmprestimo(StatusEmprestimo.FINALIZADO);

        // Atualiza status da ferramenta automaticamente
        Ferramenta ferramenta = emprestimo.getFerramenta();
        if (dados.estadoConservacao() == EstadoConservacao.DANIFICADA) {
            ferramenta.setStatus(StatusFerramenta.EM_MANUTENCAO); // RF13
        } else {
            ferramenta.setStatus(StatusFerramenta.DISPONIVEL); // RF12
        }
        ferramentaRepository.save(ferramenta);

        Emprestimo salvo = emprestimoRepository.save(emprestimo);
        return toResponseDTO(salvo);
    }

    // --- 3. LISTAGENS ---
    public List<EmprestimoResponseDTO> listarTodos() {
        return emprestimoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public EmprestimoResponseDTO buscarPorId(UUID id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new EmprestimoNaoEncontradoException(
                        "Empréstimo com id " + id + " não encontrado."));
        return toResponseDTO(emprestimo);
    }

    public List<EmprestimoResponseDTO> listarPorTecnico(UUID usuarioId) {
        return emprestimoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<EmprestimoResponseDTO> listarPorFerramenta(UUID ferramentaId) {
        return emprestimoRepository.findByFerramentaId(ferramentaId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    //Lista os empréstimos do técnico autenticado (RF06, 5.2.6)
    public List<EmprestimoResponseDTO> listarMeus() {
        Usuario usuarioAutenticado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return emprestimoRepository.findByUsuarioId(usuarioAutenticado.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // --- MAPPER ---
    private EmprestimoResponseDTO toResponseDTO(Emprestimo emprestimo) {
        return new EmprestimoResponseDTO(
                emprestimo.getId(),
                emprestimo.getUsuario().getId(),
                emprestimo.getUsuario().getNome(),
                emprestimo.getFerramenta().getId(),
                emprestimo.getFerramenta().getNome(),
                emprestimo.getFerramenta().getCodigoPatrimonio(),
                emprestimo.getDataRetirada(),
                emprestimo.getDataDevolucao(),
                emprestimo.getEstadoConservacao() != null ? emprestimo.getEstadoConservacao().name() : null,
                emprestimo.getStatusEmprestimo().name()
        );
    }
}
