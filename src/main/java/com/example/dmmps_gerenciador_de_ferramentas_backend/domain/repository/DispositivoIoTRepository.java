package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.repository;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.DispositivoIoT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DispositivoIoTRepository extends JpaRepository<DispositivoIoT, UUID> {

    // Verifica unicidade do MacAddress (RF28)
    boolean existsByMacAddress(String macAddress);

    Optional<DispositivoIoT> findByMacAddress(String macAddress);
}