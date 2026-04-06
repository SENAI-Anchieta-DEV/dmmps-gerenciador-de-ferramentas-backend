package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

import java.util.UUID;

public class FerramentaIndisponivelException extends NegocioException {
    public FerramentaIndisponivelException(UUID ferramentaId, String motivo) {
        super(String.format("A ferramenta de código %s não está disponível para empréstimo. Motivo: %s", ferramentaId, motivo));
    }
}
