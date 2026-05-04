package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusConectividade;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados retornados de um dispositivo IoT cadastrado")
public record DispositivoIoTResponseDTO(

        @Schema(description = "UUID único do dispositivo", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "MAC address do dispositivo", example = "A4:C3:F0:85:1D:3E")
        String macAddress,

        @Schema(description = "Localização física do dispositivo", example = "Almoxarifado - Prateleira B2")
        String localizacaoFisica,

        @Schema(description = "Descrição do dispositivo", example = "Leitor QR Code GM65 - ESP32")
        String descricao,

        @Schema(description = "Status de conectividade atual via MQTT", example = "ONLINE")
        StatusConectividade statusConectividade

) {}