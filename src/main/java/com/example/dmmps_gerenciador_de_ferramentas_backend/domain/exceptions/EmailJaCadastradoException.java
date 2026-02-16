package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String message) {
        super(message);
    }
}
