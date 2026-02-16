package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public abstract class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
