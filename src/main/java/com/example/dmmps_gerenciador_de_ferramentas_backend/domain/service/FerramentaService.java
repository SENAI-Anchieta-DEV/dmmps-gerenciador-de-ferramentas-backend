package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.service;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.FerramentaRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.FerramentaResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Ferramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.FerramentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FerramentaService {
    private final FerramentaRepository ferramentaRepository;

    public FerramentaService(FerramentaRepository ferramentaRepository) {
        this.ferramentaRepository = ferramentaRepository;
    }

    // --- 1. LISTAR TODAS ---
    @Transactional(readOnly = true)
    public List<FerramentaResponseDTO> listarTodas() {
        return ferramentaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // --- 2. BUSCAR POR ID ---
    @Transactional(readOnly = true)
    public FerramentaResponseDTO buscarPorId(UUID id) {
        Ferramenta ferramenta = buscarEntidadePorId(id);
        return toResponseDTO(ferramenta);
    }

    // --- 3. CADASTRAR (Create) ---
    @Transactional
    public FerramentaResponseDTO cadastrar(FerramentaRequestDTO dados) {
        // Validação simples: QR Code deve ser único
        if (ferramentaRepository.existsByCodigoQr(dados.codigoQr())) {
            // Idealmente, lançar uma exceção personalizada aqui (Task 2.4)
            throw new IllegalArgumentException("Já existe uma ferramenta com este QR Code.");
        }

        Ferramenta novaFerramenta = new Ferramenta();
        novaFerramenta.setNome(dados.nome());
        novaFerramenta.setDescricao(dados.descricao());
        novaFerramenta.setFabricante(dados.fabricante());
        novaFerramenta.setCodigoQr(dados.codigoQr());
        novaFerramenta.setGavetaLocalizacao(dados.gavetaLocalizacao());

        // Define status inicial conforme regra de negócio
        novaFerramenta.setStatus(StatusFerramenta.DISPONIVEL);

        Ferramenta salva = ferramentaRepository.save(novaFerramenta);
        return toResponseDTO(salva);
    }

    // --- 4. ATUALIZAR COMPLETO (Update) ---
    @Transactional
    public FerramentaResponseDTO atualizar(UUID id, FerramentaRequestDTO dados) {
        Ferramenta ferramenta = buscarEntidadePorId(id);

        ferramenta.setNome(dados.nome());
        ferramenta.setDescricao(dados.descricao());
        ferramenta.setFabricante(dados.fabricante());
        // Não permitimos mudar o QR Code levianamente, mas se necessário:
        ferramenta.setCodigoQr(dados.codigoQr());
        ferramenta.setGavetaLocalizacao(dados.gavetaLocalizacao());

        Ferramenta atualizada = ferramentaRepository.save(ferramenta);
        return toResponseDTO(atualizada);
    }

    // --- 5. ATUALIZAR STATUS (Patch - IoT/Empréstimo) ---
    @Transactional
    public void atualizarStatus(UUID id, StatusFerramenta novoStatus) {
        Ferramenta ferramenta = buscarEntidadePorId(id);
        ferramenta.setStatus(novoStatus);
        ferramentaRepository.save(ferramenta);
    }

    // --- 6. REMOVER (Delete) ---
    @Transactional
    public void deletar(UUID id) {
        if (!ferramentaRepository.existsById(id)) {
            throw new RuntimeException("Ferramenta não encontrada");
        }
        ferramentaRepository.deleteById(id);
    }

    // --- Métodos Auxiliares ---

    private Ferramenta buscarEntidadePorId(UUID id) {
        return ferramentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ferramenta não encontrada com id: " + id));
    }

    // Mapper manual (DTO -> Entity e Entity -> DTO)
    private FerramentaResponseDTO toResponseDTO(Ferramenta f) {
        return new FerramentaResponseDTO(
                f.getId(),
                f.getNome(),
                f.getDescricao(),
                f.getFabricante(),
                f.getCodigoQr(),
                f.getStatus(),
                f.getGavetaLocalizacao()
        );
    }
}
