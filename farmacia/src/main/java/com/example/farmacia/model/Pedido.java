package com.example.farmacia.model;

import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataPedido = LocalDateTime.now();
    private String cpfNaNota; // Aceita CPF sem exigir o vínculo obrigatório com a tabela de clientes
    
    @Enumerated(EnumType.STRING)
    private CanalVenda canalVenda; // BALCAO, INTERNET, IFOOD

    private Long vendedorId; // Usado para calcular a comissão caso seja venda de balcão
    private double valorTotal;
    private double comissaoGerada;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = true) // Nulo se o cliente não quiser se cadastrar
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public String getCpfNaNota() {
        return cpfNaNota;
    }

    public void setCpfNaNota(String cpfNaNota) {
        this.cpfNaNota = cpfNaNota;
    }

    public CanalVenda getCanalVenda() {
        return canalVenda;
    }

    public void setCanalVenda(CanalVenda canalVenda) {
        this.canalVenda = canalVenda;
    }

    public Long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getComissaoGerada() {
        return comissaoGerada;
    }

    public void setComissaoGerada(double comissaoGerada) {
        this.comissaoGerada = comissaoGerada;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }



    
}
