package com.example.notificacao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes_log")
public class NotificacaoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pedidoId;
    private String orgaoDestino;
    private String mensagemOperacao;
    private LocalDateTime dataEnvio;

    public NotificacaoLog() {}

    public NotificacaoLog(Long pedidoId, String orgaoDestino, String mensagemOperacao) {
        this.pedidoId = pedidoId;
        this.orgaoDestino = orgaoDestino;
        this.mensagemOperacao = mensagemOperacao;
        this.dataEnvio = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getPedidoId() { return pedidoId; }
    public String getOrgaoDestino() { return orgaoDestino; }
    public String getMensagemOperacao() { return mensagemOperacao; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
}