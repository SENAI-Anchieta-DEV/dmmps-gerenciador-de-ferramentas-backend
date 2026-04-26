package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.DevolucaoRequestDTO;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Emprestimo;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Ferramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.EstadoConservacao;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusEmprestimo;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.EmprestimoNaoEncontradoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.FerramentaNaoEncontradaException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID> {

    List<Emprestimo> findByUsuarioId(UUID usuarioId);

    List<Emprestimo> findByFerramentaId(UUID ferramentaId);

    List<Emprestimo> findByStatusEmprestimo(StatusEmprestimo statusEmprestimo);

    // Verifica se já existe empréstimo ABERTO para a ferramenta (RN01)
    Optional<Emprestimo> findByFerramentaIdAndStatusEmprestimo(UUID ferramentaId, StatusEmprestimo statusEmprestimo);
}
