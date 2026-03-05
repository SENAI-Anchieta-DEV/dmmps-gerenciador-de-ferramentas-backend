package com.example.dmmps_gerenciador_de_ferramentas_backend.infraestructure.security;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity.Usuario;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.PerfilUsuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Test
    @DisplayName("Deveria gerar token e recuperar o subject corretamente")
    void testeGeracaoEToken() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@empresa.com");
        usuario.setPerfil(PerfilUsuario.TECNICO);

        String token = tokenService.gerarToken(usuario);
        Assertions.assertNotNull(token);

        String subject = tokenService.getSubject(token);
        Assertions.assertEquals("teste@empresa.com", subject);
    }
}