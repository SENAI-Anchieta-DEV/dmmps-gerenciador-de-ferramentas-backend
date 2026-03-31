package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusFerramenta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.util.UUID;

@Entity
@Table(name = "ferramentas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@SoftDelete(columnName = "ativo") // Nova forma nativa do Hibernate 6.4
public class Ferramenta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // String no UML -> UUID no Java

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String fabricante;

    @Column(name = "codigo_patrimonio", unique = true, nullable = false)
    private String codigoPatrimonio; //codigoQR

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFerramenta status;

    @Column(name = "gaveta_localizacao")
    private String gavetaLocalizacao;

    public Ferramenta(String nome, String descricao, String fabricante, String codigoQr, String gavetaLocalizacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.fabricante = fabricante;
        this.codigoPatrimonio = codigoQr;
        this.gavetaLocalizacao = gavetaLocalizacao;
        this.status = StatusFerramenta.DISPONIVEL; // Status padrão ao criar
    }
}
