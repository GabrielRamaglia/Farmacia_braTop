package com.example.vendas.service;

import com.example.vendas.dto.ClienteDTO;
import com.example.vendas.dto.ProdutoDTO;
import com.example.vendas.dto.VendaRealizadaEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class IntegracaoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final RabbitTemplate rabbitTemplate;

    public IntegracaoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

   

    public ClienteDTO buscarClientePorCpf(String cpf) {
        if (cpf == null || cpf.isEmpty())
            return null;

        String url = "http://localhost:8083/clientes/" + cpf;
        try {
            return restTemplate.getForObject(url, ClienteDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    public ProdutoDTO buscarProdutoNoCatalogo(Long produtoId) {
        String url = "http://localhost:8082/produtos/" + produtoId;
        try {
            return restTemplate.getForObject(url, ProdutoDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Produto não encontrado no catálogo!");
        }
    }

    // ========================================================================
    // EVOLUÇÃO DA ARQUITETURA - NOTIFICAÇÕES (POST)
    // ========================================================================

    /*
     * VERSÃO 1: ARQUITETURA SÍNCRONA (Acoplada via HTTP)
     * Na apresentação, pode explicar que nesta versão, se o serviço de
     * notificações na porta 8084 caísse, a venda (8080) daria erro 500
     * e o cliente não conseguiria comprar o remédio.
     *
     * public void enviarNotificacaoVenda_Versao1(Long pedidoId, Double valorTotal,
     * String cpf, boolean contemControlado) {
     * String url = "http://localhost:8084/notificacoes/processar";
     * 
     * Map<String, Object> payload = new HashMap<>();
     * payload.put("pedidoId", pedidoId);
     * payload.put("valorTotal", valorTotal);
     * payload.put("cpfCliente", cpf);
     * payload.put("contemControlado", contemControlado);
     * 
     * try {
     * // O Vendas fica "bloqueado" à espera da resposta HTTP do Notificações
     * restTemplate.postForEntity(url, payload, String.class);
     * System.out.println("[Versão 1 - Síncrona] Venda " + pedidoId +
     * " notificada!");
     * } catch (Exception e) {
     * System.err.println("[Versão 1 - Erro] Falha ao notificar a venda: " +
     * e.getMessage());
     * }
     * }
     */

    /*
     * VERSÃO 2: ARQUITETURA ASSÍNCRONA / ORIENTADA A EVENTOS (Desacoplada)
     * Solução final: Usamos o RabbitMQ. O serviço de Vendas apenas
     * "emite um evento"
     * dizendo que a venda ocorreu, e segue a sua vida. Se o serviço de Notificações
     * estiver desligado, a mensagem fica guardada em segurança na fila.
     */
    public void enviarNotificacaoVenda(Long pedidoId, Double valorTotal, String cpf, boolean contemControlado) {

        VendaRealizadaEvent evento = new VendaRealizadaEvent(pedidoId, valorTotal, cpf, contemControlado);

        try {
            // Atira o evento para a Exchange do RabbitMQ e não fica à espera!
            rabbitTemplate.convertAndSend("vendas.exchange", "venda.realizada", evento);

            System.out.println(
                    "[Versão 2 - Assíncrona] Evento de Venda ID " + pedidoId + " publicado na fila com sucesso!");

        } catch (Exception e) {
            System.err.println("[Versão 2 - Erro] Falha ao publicar evento no RabbitMQ: " + e.getMessage());
        }
    }
}