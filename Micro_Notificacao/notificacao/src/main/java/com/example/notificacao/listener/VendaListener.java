package com.example.notificacao.listener;

import com.example.notificacao.dto.VendaFinalizadaDTO;
import com.example.notificacao.service.NotificacaoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class VendaListener {

    private final NotificacaoService notificacaoService;

    public VendaListener(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    // A MÁGICA ACONTECE AQUI:
    // O Spring Boot fica a ouvir a fila. Quando o Vendas atira a mensagem, 
    // ele apanha-a automaticamente, converte de JSON para DTO e processa!
    @RabbitListener(queues = "vendas.notificacoes.queue")
    public void receberVendaRealizada(VendaFinalizadaDTO vendaEvent) {
        System.out.println("==================================================");
        System.out.println("[RABBITMQ] Recebido novo evento de Venda Realizada! ID: " + vendaEvent.getPedidoId());
        
        // Passa a bola para o serviço que já havíamos construído antes (Sefaz/ANS)
        notificacaoService.processarNotificacoes(vendaEvent);
        
        System.out.println("==================================================");
    }
}