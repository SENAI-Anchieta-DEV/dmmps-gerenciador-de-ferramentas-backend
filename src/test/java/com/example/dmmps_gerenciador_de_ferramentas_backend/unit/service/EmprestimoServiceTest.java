package com.example.dmmps_gerenciador_de_ferramentas_backend.unit.service;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DevolucaoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.EmprestimoResponseDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.EmprestimoService;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Emprestimo;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Ferramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.EstadoConservacao;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.PerfilUsuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusEmprestimo;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.EmprestimoJaFinalizadoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.FerramentaIndisponivelException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.EmprestimoRepository;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.FerramentaRepository;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class EmprestimoServiceTest {
    @Mock
    private EmprestimoRepository emprestimoRepository;
    @Mock private FerramentaRepository ferramentaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    private EmprestimoService service;
    private Usuario tecnicoLogado;
    private Ferramenta ferramenta;
    private Emprestimo emprestimoAberto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new EmprestimoService(emprestimoRepository, ferramentaRepository, usuarioRepository);

        // Massa de dados comum a todos os testes
        tecnicoLogado = new Usuario("Marcus", "marcus@senai.br", "senha123", "12345", PerfilUsuario.TECNICO);
        tecnicoLogado.setId(UUID.randomUUID());

        ferramenta = new Ferramenta("Multímetro", "Multímetro Digital", "Fluke", "QR-001", "Gaveta A");
        ferramenta.setId(UUID.randomUUID());
        ferramenta.setStatus(StatusFerramenta.DISPONIVEL);

        emprestimoAberto = new Emprestimo();
        emprestimoAberto.setId(UUID.randomUUID());
        emprestimoAberto.setFerramenta(ferramenta);
        emprestimoAberto.setUsuario(tecnicoLogado);
        emprestimoAberto.setDataRetirada(LocalDateTime.now());
        emprestimoAberto.setStatusEmprestimo(StatusEmprestimo.ABERTO);
    }

    @Nested
    @DisplayName("Testes de Check-out (Retirada de Ferramenta)")
    class CheckOutTests {

        @Test
        @DisplayName("Deve realizar check-out com sucesso quando a ferramenta estiver disponível")
        void deveRealizarCheckOutComSucesso() {
            EmprestimoRequestDTO dto = new EmprestimoRequestDTO(ferramenta.getId());
            when(ferramentaRepository.findById(ferramenta.getId())).thenReturn(Optional.of(ferramenta));
            when(emprestimoRepository.findByFerramentaIdAndStatusEmprestimo(ferramenta.getId(), StatusEmprestimo.ABERTO)).thenReturn(Optional.empty());
            when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoAberto);

            EmprestimoResponseDTO response = service.realizarCheckOut(dto, tecnicoLogado);

            assertNotNull(response);
            assertEquals(StatusFerramenta.EM_USO, ferramenta.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar check-out de ferramenta que não está DISPONIVEL (RN01)")
        void deveLancarExcecaoQuandoFerramentaNaoDisponivel() {
            ferramenta.setStatus(StatusFerramenta.EM_MANUTENCAO);
            EmprestimoRequestDTO dto = new EmprestimoRequestDTO(ferramenta.getId());
            when(ferramentaRepository.findById(ferramenta.getId())).thenReturn(Optional.of(ferramenta));

            assertThrows(FerramentaIndisponivelException.class, () -> service.realizarCheckOut(dto, tecnicoLogado));
        }
    }

    @Nested
    @DisplayName("Testes de Check-in (Devolução de Ferramenta)")
    class CheckInTests {

        @Test
        @DisplayName("Deve realizar check-in com sucesso e alterar ferramenta para DISPONIVEL (RF12)")
        void deveRealizarCheckInERetornarFerramentaParaDisponivel() {
            DevolucaoRequestDTO dto = new DevolucaoRequestDTO(EstadoConservacao.BOM_ESTADO);
            when(emprestimoRepository.findById(emprestimoAberto.getId())).thenReturn(Optional.of(emprestimoAberto));
            when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoAberto);

            EmprestimoResponseDTO response = service.realizarCheckIn(emprestimoAberto.getId(), dto);

            assertNotNull(response);
            assertEquals(StatusEmprestimo.FINALIZADO, emprestimoAberto.getStatusEmprestimo());
            assertEquals(StatusFerramenta.DISPONIVEL, ferramenta.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar devolver um empréstimo já FINALIZADO")
        void deveLancarExcecaoAoDevolverEmprestimoFinalizado() {
            emprestimoAberto.setStatusEmprestimo(StatusEmprestimo.FINALIZADO);
            DevolucaoRequestDTO dto = new DevolucaoRequestDTO(EstadoConservacao.BOM_ESTADO);
            when(emprestimoRepository.findById(emprestimoAberto.getId())).thenReturn(Optional.of(emprestimoAberto));

            assertThrows(EmprestimoJaFinalizadoException.class, () -> service.realizarCheckIn(emprestimoAberto.getId(), dto));
        }
    }
}
