package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusOcorrencia;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ocorrencias")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ferramenta_id", nullable = false)
    private Ferramenta ferramenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descricao;

    // Obrigatória quando statusOcorrencia = DESCARTADA (RF24, RN06)
    @Column(length = 500)
    private String justificativaDescarte;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOcorrencia statusOcorrencia;

    @PrePersist
    public void preencherDataAbertura() {
        this.dataAbertura = LocalDateTime.now();
    }

    public Ocorrencia() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Ferramenta getFerramenta() { return ferramenta; }
    public void setFerramenta(Ferramenta ferramenta) { this.ferramenta = ferramenta; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getJustificativaDescarte() { return justificativaDescarte; }
    public void setJustificativaDescarte(String j) { this.justificativaDescarte = j; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public StatusOcorrencia getStatusOcorrencia() { return statusOcorrencia; }
    public void setStatusOcorrencia(StatusOcorrencia s) { this.statusOcorrencia = s; }
}
