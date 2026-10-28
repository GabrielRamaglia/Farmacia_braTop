package com.example.notificacao.service;

import com.example.notificacao.dto.VendaFinalizadaDTO;
import com.example.notificacao.model.NotificacaoLog;
import com.example.notificacao.repository.NotificacaoLogRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    private final NotificacaoLogRepository repository;

    public NotificacaoService(NotificacaoLogRepository repository) {
        this.repository = repository;
    }

    public void processarNotificacoes(VendaFinalizadaDTO venda) {
        enviarParaSefaz(venda);

        if (venda.isContemControlado()) {
            enviarParaAns(venda);
        }
    }

    private void enviarParaSefaz(VendaFinalizadaDTO venda) {
        String msg = "NF-e emitida com sucesso. Valor: R$ " + venda.getValorTotal() + " | CPF: " + 
                     (venda.getCpfCliente() != null ? venda.getCpfCliente() : "Consumidor Final");
        
        System.out.println("[SEFAZ] " + msg);
        repository.save(new NotificacaoLog(venda.getPedidoId(), "SEFAZ", msg));
    }

    private void enviarParaAns(VendaFinalizadaDTO venda) {
        String msg = "Receita retida e enviada para fiscalização. Paciente CPF: " + venda.getCpfCliente();
        
        System.out.println("[ANS] " + msg);
        repository.save(new NotificacaoLog(venda.getPedidoId(), "ANS", msg));
    }
}