package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class NegocioException extends RuntimeException {
    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
