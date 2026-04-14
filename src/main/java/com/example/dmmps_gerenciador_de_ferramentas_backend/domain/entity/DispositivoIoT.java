package com.example.dmmps_gerenciador_de_ferramentas_backend.domain.entity;

import com.example.dmmps_gerenciador_de_ferramentas_backend.domain.enums.StatusConectividade;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "dispositivos_iot")
public class DispositivoIoT {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // MacAddress único — validado no Service (RF28, RNF27)
    @Column(nullable = false, unique = true)
    private String macAddress;

    @Column
    private String localizacaoFisica;

    @Column
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConectividade statusConectividade = StatusConectividade.OFFLINE;

    public DispositivoIoT() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public String getLocalizacaoFisica() { return localizacaoFisica; }
    public void setLocalizacaoFisica(String localizacaoFisica) { this.localizacaoFisica = localizacaoFisica; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public StatusConectividade getStatusConectividade() { return statusConectividade; }
    public void setStatusConectividade(StatusConectividade statusConectividade) { this.statusConectividade = statusConectividade; }
}
