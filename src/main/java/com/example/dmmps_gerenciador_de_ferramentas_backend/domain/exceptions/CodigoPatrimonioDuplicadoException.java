package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions;

public class CodigoPatrimonioDuplicadoException extends NegocioException {
    public CodigoPatrimonioDuplicadoException(String codigo) {
        super(String.format("Já existe uma ferramenta cadastrada com o Código de Patrimônio %s", codigo));
    }
}
