package com.example.dmmps_gerenciador_de_ferramentas_backend.application.service;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.dto.PayloadMqttDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class AssinanteHandler {
    private static final Logger log = LoggerFactory.getLogger(AssinanteHandler.class);
    private final ObjectMapper objectMapper;
    private final EmprestimoService emprestimoService; // Adicione esta linha

    // Atualize o construtor para receber o EmprestimoService
    public AssinanteHandler(EmprestimoService emprestimoService) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.emprestimoService = emprestimoService;
    }

    @MqttSubscriber("ferramentas/leitura")
    public void processarLeitura(@MqttPayload String jsonPayload) {
        try {
            PayloadMqttDTO payload = objectMapper.readValue(jsonPayload, PayloadMqttDTO.class);
            log.info("[MQTT: Leitura] Processando ferramenta patrimônio: {} do MAC: {}",
                    payload.getCodigoPatrimonio(), payload.getMacAddress());

            // CHAMA A REGRA DE NEGÓCIO AQUI!
            emprestimoService.processarLeituraIoT(payload.getCodigoPatrimonio(), payload.getMacAddress());

        } catch (Exception e) {
            log.error("[MQTT: Leitura] Erro: {}", e.getMessage());
        }
    }

    /**
     * 2. Escuta exclusivamente os eventos de HEARTBEAT (Status online)
     */
    @MqttSubscriber("ferramentas/dispositivos/status")
    public void processarHeartbeat(@MqttPayload String jsonPayload) {
        try {
            PayloadMqttDTO payload = objectMapper.readValue(jsonPayload, PayloadMqttDTO.class);
            log.info("[MQTT: Heartbeat] Dispositivo {} está ONLINE. Timestamp: {}",
                    payload.getMacAddress(), payload.getTimestamp());

            // Lógica futura: Atualizar o "last_seen" do ESP32 no banco
        } catch (Exception e) {
            log.error("[MQTT: Heartbeat] Erro ao desserializar payload: {}", e.getMessage());
        }
    }

    /**
     * 3. Escuta exclusivamente os eventos de ERRO de hardware/leitura
     */
    @MqttSubscriber("ferramentas/erros")
    public void processarErro(@MqttPayload String jsonPayload) {
        try {
            PayloadMqttDTO payload = objectMapper.readValue(jsonPayload, PayloadMqttDTO.class);
            log.error("[MQTT: Erro] Dispositivo {} reportou ERRO. Detalhes: {}",
                    payload.getMacAddress(), payload.getDados());

            // Lógica futura: Registrar uma Ocorrencia vinculada à ferramenta ou dispositivo
        } catch (Exception e) {
            log.error("[MQTT: Erro] Erro ao desserializar payload: {}", e.getMessage());
        }
    }
}