package com.example.vendas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guarda apenas o ID, o Produto real está no MS de Catálogo (8082)
    private Long produtoId;
    
    private Integer quantidade;
    
    private Double precoUnitarioCobrado; // Preço após aplicação de descontos

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @JsonIgnore // Evita loop infinito na hora de retornar o JSON
    private Pedido pedido;

    public ItemPedido() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public Double getPrecoUnitarioCobrado() { return precoUnitarioCobrado; }
    public void setPrecoUnitarioCobrado(Double precoUnitarioCobrado) { this.precoUnitarioCobrado = precoUnitarioCobrado; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
}