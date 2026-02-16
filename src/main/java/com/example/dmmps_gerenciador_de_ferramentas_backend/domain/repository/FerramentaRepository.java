package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Ferramenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FerramentaRepository extends JpaRepository<Ferramenta, UUID> {
    boolean existsByCodigoQr(String codigoQr);
}
