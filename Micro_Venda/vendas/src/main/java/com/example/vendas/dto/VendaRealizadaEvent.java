package com.example.vendas.dto;

public class VendaRealizadaEvent {
    
    private Long pedidoId;
    private Double valorTotal;
    private String cpfCliente;
    private boolean contemControlado;

    // Construtor vazio (obrigatório para o Jackson/Spring converter de/para JSON)
    public VendaRealizadaEvent() {}

    // Construtor com todos os campos para facilitar na hora de criar o evento
    public VendaRealizadaEvent(Long pedidoId, Double valorTotal, String cpfCliente, boolean contemControlado) {
        this.pedidoId = pedidoId;
        this.valorTotal = valorTotal;
        this.cpfCliente = cpfCliente;
        this.contemControlado = contemControlado;
    }

    // Getters e Setters
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    
    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
    
    public String getCpfCliente() { return cpfCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }
    
    public boolean isContemControlado() { return contemControlado; }
    public void setContemControlado(boolean contemControlado) { this.contemControlado = contemControlado; }
}