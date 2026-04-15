package com.example.dmmps_gerenciador_de_ferramentas_backend.application.service;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class AssinanteHandler {
    @MqttSubscriber("topico/teste")
    public void receberMensagem(@MqttPayload String mensagem) {
        System.out.println("Mensagem recebida: " + mensagem);
    }
}