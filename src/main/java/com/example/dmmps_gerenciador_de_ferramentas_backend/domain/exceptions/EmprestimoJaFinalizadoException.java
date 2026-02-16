package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class EmprestimoJaFinalizadoException extends RuntimeException {
    public EmprestimoJaFinalizadoException(String message) {
        super(message);
    }
}
