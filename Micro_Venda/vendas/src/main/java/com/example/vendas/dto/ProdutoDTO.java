package com.example.vendas.dto;

// Classe simples apenas para receber os dados do MS Catálogo
public class ProdutoDTO {
    private Long id;
    private String nome;
    private Double precoBase;
    private String tipoProduto;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Double getPrecoBase() { return precoBase; }
    public void setPrecoBase(Double precoBase) { this.precoBase = precoBase; }
    public String getTipoProduto() { return tipoProduto; }
    public void setTipoProduto(String tipoProduto) { this.tipoProduto = tipoProduto; }
}