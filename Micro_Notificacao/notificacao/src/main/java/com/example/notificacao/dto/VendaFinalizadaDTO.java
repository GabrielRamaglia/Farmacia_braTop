package com.example.notificacao.dto;

public class VendaFinalizadaDTO {
    
    private Long pedidoId;
    private Double valorTotal;
    private String cpfCliente;
    private boolean contemControlado;

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
    public String getCpfCliente() { return cpfCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }
    public boolean isContemControlado() { return contemControlado; }
    public void setContemControlado(boolean contemControlado) { this.contemControlado = contemControlado; }
}