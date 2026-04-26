package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.TipoEventoMQTT;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class PayloadMqttDTO {
    private String macAddress;

    private String codigoPatrimonio;

    private TipoEventoMQTT tipo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private Map<String, Object> dados;
}
