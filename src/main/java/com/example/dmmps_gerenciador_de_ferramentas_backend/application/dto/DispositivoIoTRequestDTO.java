package com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DispositivoIoTRequestDTO(
        @NotBlank
        @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$",
                message = "MacAddress inválido. Use o formato XX:XX:XX:XX:XX:XX")
        String macAddress,

        String localizacaoFisica,

        String descricao
) {
}
