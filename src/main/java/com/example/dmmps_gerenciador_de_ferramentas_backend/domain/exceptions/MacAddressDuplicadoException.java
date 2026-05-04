package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class MacAddressDuplicadoException extends NegocioException {
    public MacAddressDuplicadoException(String macAddress) {
        super("Já existe um dispositivo cadastrado com o MacAddress: " + macAddress);
    }
}