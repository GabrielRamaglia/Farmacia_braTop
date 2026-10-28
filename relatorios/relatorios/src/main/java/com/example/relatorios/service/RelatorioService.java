package com.example.relatorios.service;

import com.example.relatorios.dto.ModelosDTO.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

  private final RestTemplate restTemplate = new RestTemplate();

  // 1. RELATÓRIO DE ESTOQUE (Busca no Catálogo)
  public List<ProdutoDTO> relatorioEstoqueBaixo() {
    String url = "http://localhost:8082/produtos";
    List<ProdutoDTO> todosProdutos = restTemplate.exchange(
        url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ProdutoDTO>>() {
        }).getBody();

    if (todosProdutos == null)
      return new ArrayList<>();

    // Retorna apenas produtos com menos de 20 itens no estoque
    return todosProdutos.stream()
        .filter(p -> p.quantidadeEstoque < 20)
        .collect(Collectors.toList());
  }

  // 2. RELATÓRIO DE VENDAS (Busca nas Vendas)
  public List<PedidoDTO> relatorioTodasVendas() {
    String url = "http://localhost:8080/pedidos";
    return restTemplate.exchange(
        url, HttpMethod.GET, null, new ParameterizedTypeReference<List<PedidoDTO>>() {
        }).getBody();
  }

  // 3. RELATÓRIO DE MEDICAMENTOS MAIS VENDIDOS (Busca nas Vendas e no Catálogo)
  public List<ProdutoMaisVendidoDTO> relatorioMaisVendidos() {
    List<PedidoDTO> vendas = relatorioTodasVendas();
    if (vendas == null)
      return new ArrayList<>();

    // Conta a quantidade vendida de cada produto ID
    Map<Long, Integer> contagem = new HashMap<>();
    for (PedidoDTO pedido : vendas) {
      for (ItemPedidoDTO item : pedido.itens) {
        contagem.put(item.produtoId, contagem.getOrDefault(item.produtoId, 0) + item.quantidade);
      }
    }

    // Busca os nomes no catálogo
    String urlCatalogo = "http://localhost:8082/produtos";
    List<ProdutoDTO> produtos = restTemplate.exchange(
        urlCatalogo, HttpMethod.GET, null, new ParameterizedTypeReference<List<ProdutoDTO>>() {
        }).getBody();

    List<ProdutoMaisVendidoDTO> relatorio = new ArrayList<>();

    for (Map.Entry<Long, Integer> entry : contagem.entrySet()) {
      Long pId = entry.getKey();
      String nome = "Desconhecido";

      if (produtos != null) {
        nome = produtos.stream()
            .filter(p -> p.id.equals(pId))
            .map(p -> p.nome)
            .findFirst()
            .orElse("Desconhecido");
      }
      relatorio.add(new ProdutoMaisVendidoDTO(pId, nome, entry.getValue()));
    }

    // Ordena do mais vendido para o menos vendido
    relatorio.sort((a, b) -> b.totalVendido.compareTo(a.totalVendido));
    return relatorio;
  }
}