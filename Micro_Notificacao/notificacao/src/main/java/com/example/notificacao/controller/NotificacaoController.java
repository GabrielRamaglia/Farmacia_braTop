package com.example.notificacao.controller;

import com.example.notificacao.dto.VendaFinalizadaDTO;
import com.example.notificacao.service.NotificacaoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @PostMapping("/processar")
    public ResponseEntity<String> receberVendaFinalizada(@RequestBody VendaFinalizadaDTO vendaDTO) {
        notificacaoService.processarNotificacoes(vendaDTO);
        return ResponseEntity.ok("Notificações processadas e registradas com sucesso.");
    }
}