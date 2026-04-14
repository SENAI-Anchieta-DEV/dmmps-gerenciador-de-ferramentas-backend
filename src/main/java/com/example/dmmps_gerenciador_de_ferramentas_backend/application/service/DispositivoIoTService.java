package com.example.dmmps_gerenciador_de_ferramentas_backend.application.service;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DispositivoIoTRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DispositivoIoTResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.DispositivoIoT;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusConectividade;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.DispositivoNaoEncontradoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.MacAddressDuplicadoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.DispositivoIoTRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DispositivoIoTService {

    private final DispositivoIoTRepository dispositivoRepository;

    public DispositivoIoTService(DispositivoIoTRepository dispositivoRepository) {
        this.dispositivoRepository = dispositivoRepository;
    }

    // --- 1. CADASTRAR (RF28) ---
    @Transactional
    public DispositivoIoTResponseDTO cadastrar(DispositivoIoTRequestDTO dados) {
        // Valida unicidade do MacAddress
        if (dispositivoRepository.existsByMacAddress(dados.macAddress())) {
            throw new MacAddressDuplicadoException(dados.macAddress());
        }

        DispositivoIoT dispositivo = new DispositivoIoT();
        dispositivo.setMacAddress(dados.macAddress());
        dispositivo.setLocalizacaoFisica(dados.localizacaoFisica());
        dispositivo.setDescricao(dados.descricao());
        dispositivo.setStatusConectividade(StatusConectividade.OFFLINE);
        dispositivo.setMacAddress(dados.macAddress().toUpperCase().replace("-", ":"));

        DispositivoIoT salvo = dispositivoRepository.save(dispositivo);
        return toResponseDTO(salvo);
    }

    // --- 2. LISTAR TODOS ---
    public List<DispositivoIoTResponseDTO> listarTodos() {
        return dispositivoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // --- 3. BUSCAR POR ID ---
    public DispositivoIoTResponseDTO buscarPorId(UUID id) {
        DispositivoIoT dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new DispositivoNaoEncontradoException(id));
        return toResponseDTO(dispositivo);
    }

    // --- 4. ATUALIZAR (localização e descrição) ---
    @Transactional
    public DispositivoIoTResponseDTO atualizar(UUID id, DispositivoIoTRequestDTO dados) {
        DispositivoIoT dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new DispositivoNaoEncontradoException(id));

        // Verifica se o novo MacAddress já pertence a outro dispositivo
        if (!dispositivo.getMacAddress().equals(dados.macAddress())
                && dispositivoRepository.existsByMacAddress(dados.macAddress())) {
            throw new MacAddressDuplicadoException(dados.macAddress());
        }

        dispositivo.setMacAddress(dados.macAddress());
        dispositivo.setLocalizacaoFisica(dados.localizacaoFisica());
        dispositivo.setDescricao(dados.descricao());
        String macNormalizado = dados.macAddress().toUpperCase().replace("-", ":");

        if (!dispositivo.getMacAddress().equals(macNormalizado)
                && dispositivoRepository.existsByMacAddress(macNormalizado)) {
            throw new MacAddressDuplicadoException(macNormalizado);
        }


        DispositivoIoT salvo = dispositivoRepository.save(dispositivo);
        return toResponseDTO(salvo);
    }

    // --- 5. REMOVER ---
    @Transactional
    public void remover(UUID id) {
        DispositivoIoT dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new DispositivoNaoEncontradoException(id));
        dispositivoRepository.delete(dispositivo);
    }

    // --- 6. CONSULTAR STATUS DE CONECTIVIDADE (RF29) ---
    public StatusConectividade consultarStatus(UUID id) {
        DispositivoIoT dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new DispositivoNaoEncontradoException(id));
        return dispositivo.getStatusConectividade();
    }

    // --- ATUALIZAR STATUS VIA MQTT (chamado pelo Listener MQTT) ---
    @Transactional
    public void atualizarStatusConectividade(String macAddress, StatusConectividade status) {
        dispositivoRepository.findByMacAddress(macAddress)
                .ifPresent(dispositivo -> {
                    dispositivo.setStatusConectividade(status);
                    dispositivoRepository.save(dispositivo);
                });
        // Se MacAddress não cadastrado, ignora silenciosamente (RNF27)
    }

    // --- MAPPER ---
    private DispositivoIoTResponseDTO toResponseDTO(DispositivoIoT dispositivo) {
        return new DispositivoIoTResponseDTO(
                dispositivo.getId(),
                dispositivo.getMacAddress(),
                dispositivo.getLocalizacaoFisica(),
                dispositivo.getDescricao(),
                dispositivo.getStatusConectividade()
        );
    }
}
