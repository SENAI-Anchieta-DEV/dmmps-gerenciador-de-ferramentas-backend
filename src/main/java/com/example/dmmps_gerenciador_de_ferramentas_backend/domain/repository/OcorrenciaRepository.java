package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Ocorrencia;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, UUID> {

    List<Ocorrencia> findByFerramentaId(UUID ferramentaId);       // RF34 - histórico por ferramenta

    List<Ocorrencia> findByStatusOcorrencia(StatusOcorrencia statusOcorrencia); // filtrar por status

    List<Ocorrencia> findByUsuarioId(UUID usuarioId);             // ocorrências por técnico
}
