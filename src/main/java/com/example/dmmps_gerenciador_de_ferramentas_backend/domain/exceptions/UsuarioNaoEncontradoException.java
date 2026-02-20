package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class UsuarioNaoEncontradoException extends RecursoNaoEncontradoException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
