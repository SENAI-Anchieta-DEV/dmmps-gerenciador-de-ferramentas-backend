package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.exception;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.EntidadeEmUsoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.NegocioException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // --- 404 NOT FOUND ---
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // --- 409 CONFLICT (Duplicidade/Integridade) ---
    @ExceptionHandler(EntidadeEmUsoException.class)
    public ProblemDetail handleEntidadeEmUso(EntidadeEmUsoException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.CONFLICT,
                "Conflito de dados",
                ex.getMessage(), // Ex: "Já existe uma ferramenta com este QR Code"
                request.getRequestURI()
        );
    }

    // --- 400 BAD REQUEST (Erro de Negócio genérico) ---
    @ExceptionHandler(NegocioException.class)
    public ProblemDetail handleNegocioException(NegocioException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Violação de regra de negócio",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // --- 400 BAD REQUEST (Validação de Campos @Valid) ---
    // Este pega erros automáticos do Spring Validation (ex: @NotNull, @Email)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                "Um ou mais campos estão inválidos",
                request.getRequestURI()
        );

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        problem.setProperty("errors", errors);
        return problem;
    }

    // --- 500 INTERNAL SERVER ERROR (Genérico) ---
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex, HttpServletRequest request) {
        // Logar o erro real no console é importante aqui
        ex.printStackTrace();

        return ProblemDetailUtils.buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Contate o suporte.",
                request.getRequestURI()
        );
    }

    /* AGUARDANDO IMPLEMENTAÇÃO DO SPRING SECURITY
    // --- 401 UNAUTHORIZED ---
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(Exception ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.UNAUTHORIZED,
                "Não autenticado",
                "Você precisa estar logado para acessar este recurso.",
                request.getRequestURI()
        );
    }

    // --- 403 FORBIDDEN ---
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(Exception ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.FORBIDDEN,
                "Acesso proibido",
                "Você não tem permissão para realizar esta ação.",
                request.getRequestURI()
        );
    }*/

}
