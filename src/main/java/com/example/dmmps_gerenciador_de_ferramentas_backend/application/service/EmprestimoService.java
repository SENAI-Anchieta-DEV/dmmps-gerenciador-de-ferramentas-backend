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
    public EmprestimoResponseDTO realizarCheckOut(EmprestimoRequestDTO dados, Usuario usuarioAutenticado) {
        Ferramenta ferramenta = ferramentaRepository.findById(dados.ferramentaId())
                .orElseThrow(() -> new FerramentaNaoEncontradaException(dados.ferramentaId()));

        // RN01 — Impede check-out se ferramenta não estiver DISPONIVEL
        if (ferramenta.getStatus() != StatusFerramenta.DISPONIVEL) {
            throw new FerramentaIndisponivelException(dados.ferramentaId(), "Status atual: " + ferramenta.getStatus().name());
        }

        // RF33 — Impede dois técnicos de pegar a mesma ferramenta simultaneamente
        emprestimoRepository.findByFerramentaIdAndStatusEmprestimo(dados.ferramentaId(), StatusEmprestimo.ABERTO)
                .ifPresent(e -> { throw new FerramentaIndisponivelException(dados.ferramentaId(), "Já existe um empréstimo em aberto para esta ferramenta."); });

        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setFerramenta(ferramenta);
        novoEmprestimo.setUsuario(usuarioAutenticado); // Usuário vem do JWT
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

        if (emprestimo.getStatusEmprestimo() == StatusEmprestimo.FINALIZADO) {
            throw new EmprestimoJaFinalizadoException("Empréstimo com id " + id + " já foi finalizado.");
        }

        emprestimo.setEstadoConservacao(dados.estadoConservacao());
        emprestimo.setDataDevolucao(LocalDateTime.now());
        emprestimo.setStatusEmprestimo(StatusEmprestimo.FINALIZADO);

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

    // --- 4. INTEGRAÇÃO IOT (MQTT) ---
    @Transactional
    public void processarLeituraIoT(String codigoPatrimonio, String macAddress) {
        Ferramenta ferramenta = ferramentaRepository.findByCodigoPatrimonio(codigoPatrimonio)
                .orElseThrow(() -> new FerramentaNaoEncontradaException(UUID.randomUUID()));

        // ==========================================
        // FLUXO DE RETIRADA (CHECK-OUT)
        // ==========================================
        if (ferramenta.getStatus() == StatusFerramenta.DISPONIVEL) {
            System.out.println(">>> [IoT] Lida ferramenta no RFID: " + ferramenta.getNome());

            // 1. Procura se alguém solicitou esta ferramenta pelo App
            // Como você já tem esse método no repository, vamos reutilizá-hor
            Emprestimo reserva = emprestimoRepository.findByFerramentaIdAndStatusEmprestimo(ferramenta.getId(), StatusEmprestimo.AGUARDANDO_RETIRADA)
                    .orElseThrow(() -> new NegocioException("Tentativa de retirada bloqueada: Nenhuma solicitação via app foi feita para esta ferramenta."));

            // 2. Se achou a reserva, efetiva o empréstimo!
            reserva.setStatusEmprestimo(StatusEmprestimo.ABERTO);
            reserva.setDataRetirada(LocalDateTime.now());

            ferramenta.setStatus(StatusFerramenta.EM_USO);

            emprestimoRepository.save(reserva);
            ferramentaRepository.save(ferramenta);

            System.out.println(">>> [IoT] SUCESSO! Ferramenta vinculada ao técnico: " + reserva.getUsuario().getNome());

            // ==========================================
            // FLUXO DE DEVOLUÇÃO (CHECK-IN)
            // ==========================================
        } else if (ferramenta.getStatus() == StatusFerramenta.EM_USO) {
            System.out.println(">>> [IoT] Iniciando DEVOLUÇÃO da ferramenta: " + ferramenta.getNome());

            Emprestimo emprestimoAberto = emprestimoRepository.findByFerramentaIdAndStatusEmprestimo(ferramenta.getId(), StatusEmprestimo.ABERTO)
                    .orElseThrow(() -> new EmprestimoNaoEncontradoException("Nenhum empréstimo aberto para esta ferramenta."));

            // Efetiva a devolução chamando o seu método existente
            DevolucaoRequestDTO devolucaoDTO = new DevolucaoRequestDTO(EstadoConservacao.BOM_ESTADO);
            this.realizarCheckIn(emprestimoAberto.getId(), devolucaoDTO);

            System.out.println(">>> [IoT] Devolução concluída com sucesso!");

        } else {
            System.err.println(">>> [IoT] ALERTA: A ferramenta " + ferramenta.getNome() + " está em status inválido: " + ferramenta.getStatus());
        }
    }

    // --- NOVO: SOLICITAÇÃO VIA APP/WEB ---
    @Transactional
    public EmprestimoResponseDTO solicitarFerramenta(UUID ferramentaId, Usuario usuarioAutenticado) {
        Ferramenta ferramenta = ferramentaRepository.findById(ferramentaId)
                .orElseThrow(() -> new FerramentaNaoEncontradaException(ferramentaId));

        if (ferramenta.getStatus() != StatusFerramenta.DISPONIVEL) {
            throw new FerramentaIndisponivelException(ferramentaId, "Status atual: " + ferramenta.getStatus().name());
        }

        // 1. Cria a intenção de empréstimo (Reserva)
        Emprestimo pendente = new Emprestimo();
        pendente.setFerramenta(ferramenta);
        pendente.setUsuario(usuarioAutenticado);
        pendente.setStatusEmprestimo(StatusEmprestimo.AGUARDANDO_RETIRADA); // Fica em espera!

        // 2. Salva no banco
        Emprestimo salvo = emprestimoRepository.save(pendente);

        // TODO: Aqui você vai injetar o seu PublicadorService e enviar a mensagem MQTT
        // Exemplo: publicadorService.publicar("toolhub/feedback", "LIBERAR");
        System.out.println(">>> [API] Solicitação criada. Gaveta liberada para: " + ferramenta.getNome());

        return toResponseDTO(salvo);
    }
}