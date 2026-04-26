package com.example.dmmps_gerenciador_de_ferramentas_backend.interface_ui.controller;

import com.example.dmmps_gerenciador_de_ferramentas_backend.application.service.PublicadorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste-mqtt")
public class TesteController {
    private final PublicadorService publicadorService;

    public TesteController(PublicadorService publicadorService) {
        this.publicadorService = publicadorService;
    }

    @GetMapping("/enviar")
    public String dispararMqtt() {
        return publicadorService.publicarMensagem();
    }
}
