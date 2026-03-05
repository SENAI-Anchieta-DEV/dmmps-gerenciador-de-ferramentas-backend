package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class TransicaoStatusInvalidaException extends NegocioException {
    public TransicaoStatusInvalidaException(String message) {
        super(message);
    }
}