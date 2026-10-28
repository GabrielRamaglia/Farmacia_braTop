package com.example.relatorios.dto;

import java.util.List;

// Juntei as classes DTO num único ficheiro para facilitar a sua vida!
public class ModelosDTO {

    public static class ProdutoDTO {
        public Long id;
        public String nome;
        public Integer quantidadeEstoque;
        public String tipoProduto;
    }

    public static class PedidoDTO {
        public Long id;
        public Double total;
        public List<ItemPedidoDTO> itens;
    }

    public static class ItemPedidoDTO {
        public Long produtoId;
        public Integer quantidade;
    }
    
    // DTO específico para o Relatório de Mais Vendidos
    public static class ProdutoMaisVendidoDTO {
        public Long produtoId;
        public String nomeProduto;
        public Integer totalVendido;

        public ProdutoMaisVendidoDTO(Long produtoId, String nomeProduto, Integer totalVendido) {
            this.produtoId = produtoId;
            this.nomeProduto = nomeProduto;
            this.totalVendido = totalVendido;
        }
    }
}