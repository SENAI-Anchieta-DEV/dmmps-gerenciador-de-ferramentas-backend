package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.EstadoConservacao;
import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusEmprestimo;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "emprestimos")
public class Emprestimo {

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
    private LocalDateTime dataRetirada;

    @Column
    private LocalDateTime dataDevolucao;

    // Informado pelo Técnico na devolução (RF12, RN03)
    @Enumerated(EnumType.STRING)
    @Column
    private EstadoConservacao estadoConservacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEmprestimo statusEmprestimo;

    @PrePersist
    public void preencherDataRetirada() {
        this.dataRetirada = LocalDateTime.now();
    }

    public Emprestimo() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Ferramenta getFerramenta() { return ferramenta; }
    public void setFerramenta(Ferramenta ferramenta) { this.ferramenta = ferramenta; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(LocalDateTime dataRetirada) { this.dataRetirada = dataRetirada; }

    public LocalDateTime getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDateTime dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public EstadoConservacao getEstadoConservacao() { return estadoConservacao; }
    public void setEstadoConservacao(EstadoConservacao estadoConservacao) { this.estadoConservacao = estadoConservacao; }

    public StatusEmprestimo getStatusEmprestimo() { return statusEmprestimo; }
    public void setStatusEmprestimo(StatusEmprestimo statusEmprestimo) { this.statusEmprestimo = statusEmprestimo; }
}