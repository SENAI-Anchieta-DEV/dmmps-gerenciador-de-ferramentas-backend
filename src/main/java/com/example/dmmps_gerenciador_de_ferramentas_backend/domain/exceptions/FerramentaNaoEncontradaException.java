package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

import java.util.UUID;

public class FerramentaNaoEncontradaException extends RecursoNaoEncontradoException {
    public FerramentaNaoEncontradaException(UUID id) {
        super(String.format("Não existe um cadastro de ferramenta com código %d", id));
    }
    public FerramentaNaoEncontradaException(String message) {
    super(message);
    }
}
