package com.example.dmmps_gerenciador_de_ferramentas_backend.application.service;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.AtualizacaoStatusOcorrenciaDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.OcorrenciaRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.OcorrenciaResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Ferramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Ocorrencia;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusOcorrencia;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.FerramentaNaoEncontradaException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.OcorrenciaNaoEncontradaException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.TransicaoStatusInvalidaException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.UsuarioNaoEncontradoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.FerramentaRepository;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.OcorrenciaRepository;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final FerramentaRepository ferramentaRepository;
    private final UsuarioRepository usuarioRepository;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository,
                             FerramentaRepository ferramentaRepository,
                             UsuarioRepository usuarioRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.ferramentaRepository = ferramentaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // --- 1. REGISTRAR OCORRÊNCIA (RF14) ---
    @Transactional
    public OcorrenciaResponseDTO registrarOcorrencia(OcorrenciaRequestDTO dados) {
        Ferramenta ferramenta = ferramentaRepository.findById(dados.ferramentaId())
                .orElseThrow(() -> new FerramentaNaoEncontradaException("Ferramenta não encontrada com id: " + dados.ferramentaId()));

        Usuario usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com id: "));

        Ocorrencia novaOcorrencia = new Ocorrencia();
        novaOcorrencia.setFerramenta(ferramenta);
        novaOcorrencia.setUsuario(usuario);
        novaOcorrencia.setTitulo(dados.titulo());
        novaOcorrencia.setDescricao(dados.descricao());
        novaOcorrencia.setStatusOcorrencia(StatusOcorrencia.EM_MANUTENCAO);

        // Atualiza status da ferramenta automaticamente (RF14)
        ferramenta.setStatus(StatusFerramenta.EM_MANUTENCAO);
        ferramentaRepository.save(ferramenta);

        Ocorrencia salva = ocorrenciaRepository.save(novaOcorrencia);
        return toResponseDTO(salva);
    }

    // --- 2. ATUALIZAR STATUS (RF15, RF16, RN06) ---
    @Transactional
    public OcorrenciaResponseDTO atualizarStatus(UUID idOcorrencia, AtualizacaoStatusOcorrenciaDTO dados) {
        Ocorrencia ocorrencia = buscarEntidadePorId(idOcorrencia);

        // Valida justificativa obrigatória ao descartar (RF24, RN06)
        if (dados.statusOcorrencia() == StatusOcorrencia.DESCARTADA) {
            if (dados.justificativaDescarte() == null || dados.justificativaDescarte().isBlank()) {
                throw new TransicaoStatusInvalidaException("A justificativa é obrigatória ao descartar uma ocorrência");
            }
            ocorrencia.setJustificativaDescarte(dados.justificativaDescarte());

            // Atualiza ferramenta para DESCARTADA (RF16)
            ocorrencia.getFerramenta().setStatus(StatusFerramenta.DESCARTADA);
        }

        // Ao resolver, retorna ferramenta para DISPONIVEL (RF15)
        if (dados.statusOcorrencia() == StatusOcorrencia.RESOLVIDA) {
            ocorrencia.getFerramenta().setStatus(StatusFerramenta.DISPONIVEL);
        }

        ferramentaRepository.save(ocorrencia.getFerramenta());
        ocorrencia.setStatusOcorrencia(dados.statusOcorrencia());

        Ocorrencia atualizada = ocorrenciaRepository.save(ocorrencia);
        return toResponseDTO(atualizada);
    }

    // --- 3. LISTAR TODAS ---
    @Transactional(readOnly = true)
    public List<OcorrenciaResponseDTO> listarTodas() {
        return ocorrenciaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // --- 4. BUSCAR POR ID ---
    @Transactional(readOnly = true)
    public OcorrenciaResponseDTO buscarPorId(UUID id) {
        return toResponseDTO(buscarEntidadePorId(id));
    }

    // --- 5. LISTAR POR FERRAMENTA (RF34) ---
    @Transactional(readOnly = true)
    public List<OcorrenciaResponseDTO> listarPorFerramenta(UUID idFerramenta) {
        return ocorrenciaRepository.findByFerramentaId(idFerramenta).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // --- Métodos Auxiliares ---

    private Ocorrencia buscarEntidadePorId(UUID id) {
        return ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new OcorrenciaNaoEncontradaException("Ocorrência não encontrada com id: " + id));
    }

    private OcorrenciaResponseDTO toResponseDTO(Ocorrencia o) {
        return new OcorrenciaResponseDTO(
                o.getId(),
                o.getFerramenta().getId(),
                o.getUsuario().getId(),
                o.getTitulo(),
                o.getDescricao(),
                o.getStatusOcorrencia(),
                o.getDataAbertura()
        );
    }

    @Transactional(readOnly = true)
    public List<OcorrenciaResponseDTO> listarPorUsuario(UUID idUsuario) {
        return ocorrenciaRepository.findByUsuarioId(idUsuario)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }
}
