package com.example.vendas.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guarda o CPF para buscar os dados no MS de Clientes (8083)
    private String cpfCliente;
    
    private Long vendedorId; // Para cálculo de comissão
    
    private Double total;
    
    private Double comissao;

    @Enumerated(EnumType.STRING)
    private CanalVenda canalVenda;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCpfCliente() { return cpfCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }
    public Long getVendedorId() { return vendedorId; }
    public void setVendedorId(Long vendedorId) { this.vendedorId = vendedorId; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public Double getComissao() { return comissao; }
    public void setComissao(Double comissao) { this.comissao = comissao; }
    public CanalVenda getCanalVenda() { return canalVenda; }
    public void setCanalVenda(CanalVenda canalVenda) { this.canalVenda = canalVenda; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
}