package com.example.dmmps_gerenciador_de_ferramentas_backend.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder; // injetado do PasswordEncoderConfig

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/recuperar-senha").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Usuários — somente ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/**").hasRole("ADMIN")

                        // Ferramentas — somente ALMOXARIFE
                        .requestMatchers(HttpMethod.POST, "/api/v1/ferramentas").hasRole("ALMOXARIFE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/ferramentas/**").hasRole("ALMOXARIFE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/ferramentas/**").hasRole("ALMOXARIFE")

                        // Empréstimos — somente TECNICO
                        .requestMatchers(HttpMethod.POST, "/api/v1/emprestimos").hasRole("TECNICO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/emprestimos/**").hasRole("TECNICO")

                        // Qualquer outra rota exige autenticação
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder); // usa o campo injetado, não metodo local
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}