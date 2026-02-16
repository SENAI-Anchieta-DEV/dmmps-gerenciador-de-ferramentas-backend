package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
