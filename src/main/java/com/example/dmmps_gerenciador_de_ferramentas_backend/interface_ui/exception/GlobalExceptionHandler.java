package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.exception;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.EntidadeEmUsoException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.FerramentaIndisponivelException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.NegocioException;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.exceptions.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.access.AccessDeniedException;
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

    // --- 409 CONFLICT (Ferramenta já emprestada - RN01, RF33) ---
    @ExceptionHandler(FerramentaIndisponivelException.class)
    public ProblemDetail handleFerramentaIndisponivel(FerramentaIndisponivelException ex, HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.CONFLICT,
                "Ferramenta indisponível",
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

    // 1. Usuário não existe ou senha errada (O Spring Security não diferencia os dois por segurança)
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos.");
        pd.setTitle("Credenciais Inválidas");
        return pd;
    }

    // 2. O usuário existe e a senha está certa, mas o campo "ativo" está como false no banco
    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabledException(DisabledException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Sua conta está inativa. Entre em contato com o administrador.");
        pd.setTitle("Usuário Inativo");
        return pd;
    }

    // 3. O usuário tentou acessar uma rota sem enviar o Token JWT (ou o token expirou)
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Autenticação ausente ou token inválido/expirado.");
        pd.setTitle("Não Autenticado");
        return pd;
    }

    // 4. O usuário mandou um Token válido, mas o perfil dele não tem permissão para a rota (ex: TECNICO tentando ver lista de usuários)
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso.");
        pd.setTitle("Acesso Negado");
        return pd;
    }

}
