package com.example.dmmps_gerenciador_de_ferramentas_backend.application.service;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPublisher;
import org.springframework.stereotype.Service;

@Service
public class PublicadorService {
    @MqttPublisher("topico/teste")
    public String publicarMensagem() {
        return "Olá MQTT!";
    }
}
