package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class EntidadeEmUsoException extends RuntimeException {
    public EntidadeEmUsoException(String mensagem) {
        super(mensagem);
    }
}
