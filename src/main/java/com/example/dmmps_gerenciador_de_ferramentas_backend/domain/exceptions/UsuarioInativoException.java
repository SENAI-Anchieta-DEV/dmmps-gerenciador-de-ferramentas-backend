package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class UsuarioInativoException extends RuntimeException {
    public UsuarioInativoException(String message) {
        super(message);
    }
}
