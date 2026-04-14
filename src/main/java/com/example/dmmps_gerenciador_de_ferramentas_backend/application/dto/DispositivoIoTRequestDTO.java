package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Dados para cadastro ou atualização de um dispositivo IoT")
public record DispositivoIoTRequestDTO(

        @Schema(description = "MAC address do dispositivo no formato XX:XX:XX:XX:XX:XX", example = "A4:C3:F0:85:1D:3E")
        @NotBlank
        @Pattern(regexp = "^([0-9A-Fa-f]{2}:){5}([0-9A-Fa-f]{2})$",
                message = "MacAddress inválido. Use o formato XX:XX:XX:XX:XX:XX")
        String macAddress,

        @Schema(description = "Localização física do dispositivo na planta", example = "Almoxarifado - Prateleira B2")
        String localizacaoFisica,

        @Schema(description = "Descrição adicional do dispositivo", example = "Leitor QR Code GM65 - ESP32")
        String descricao

) {}