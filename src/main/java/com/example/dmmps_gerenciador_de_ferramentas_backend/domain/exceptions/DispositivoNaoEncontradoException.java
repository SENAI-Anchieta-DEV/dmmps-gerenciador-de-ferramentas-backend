package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

import java.util.UUID;

public class DispositivoNaoEncontradoException extends RecursoNaoEncontradoException {
    public DispositivoNaoEncontradoException(UUID id) {
        super("Dispositivo IoT com id " + id + " não encontrado.");
    }
}
