package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class OcorrenciaNaoEncontradaException extends RuntimeException {
    public OcorrenciaNaoEncontradaException(String message) {
        super(message);
    }
}
