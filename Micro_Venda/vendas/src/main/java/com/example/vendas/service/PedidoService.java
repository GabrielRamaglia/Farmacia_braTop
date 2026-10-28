package com.example.vendas.service;

import com.example.vendas.dto.ClienteDTO;
import com.example.vendas.dto.ProdutoDTO;
import com.example.vendas.model.CanalVenda;
import com.example.vendas.model.ItemPedido;
import com.example.vendas.model.Pedido;
import com.example.vendas.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final IntegracaoService integracaoService;

    public PedidoService(PedidoRepository pedidoRepository, IntegracaoService integracaoService) {
        this.pedidoRepository = pedidoRepository;
        this.integracaoService = integracaoService;
    }

    public Pedido processarVenda(Pedido pedido) {
        double valorTotalPedido = 0.0;
        boolean contemControlado = false; // VARIÁVEL NOVA PARA RASTREAR MEDICAMENTOS CONTROLADOS

        // 1. Busca os dados do cliente no Microsserviço de Clientes (se o CPF foi
        // informado)
        ClienteDTO cliente = integracaoService.buscarClientePorCpf(pedido.getCpfCliente());

        // 2. Processa cada item do carrinho
        for (ItemPedido item : pedido.getItens()) {
            // Liga a FK no banco de dados
            item.setPedido(pedido);

            // 3. Busca os dados reais do produto no Microsserviço de Catálogo
            ProdutoDTO produto = integracaoService.buscarProdutoNoCatalogo(item.getProdutoId());

            // 4. Regra: Controle de CPF para medicamentos controlados
            if ("CONTROLADO".equals(produto.getTipoProduto())) {
                contemControlado = true; // MARCAMOS QUE O CARRINHO TEM CONTROLADO
                if (pedido.getCpfCliente() == null) {
                    throw new RuntimeException(
                            "Venda Bloqueada: O produto " + produto.getNome() + " exige o CPF do cliente.");
                }
            }

            // 5. Regras de Desconto
            double precoFinal = produto.getPrecoBase();

            if (cliente != null) {
                boolean isMedicamento = "MEDICAMENTO".equals(produto.getTipoProduto())
                        || "CONTROLADO".equals(produto.getTipoProduto());

                // Descontos aplicados (20% Idoso, 10% Convénio)
                if (isMedicamento && cliente.isIdoso()) {
                    precoFinal = precoFinal * 0.80;
                } else if (isMedicamento && cliente.isPossuiConvenio()) {
                    precoFinal = precoFinal * 0.90;
                }
            }

            item.setPrecoUnitarioCobrado(precoFinal);
            valorTotalPedido += (precoFinal * item.getQuantidade());
        }

        pedido.setTotal(valorTotalPedido);

        // 6. Cálculo de comissões
        double comissao = 0.0;
        if (pedido.getCanalVenda() == CanalVenda.BALCAO) {
            comissao = valorTotalPedido * 0.05;
        } else if (pedido.getCanalVenda() == CanalVenda.SITE || pedido.getCanalVenda() == CanalVenda.IFOOD) {
            comissao = valorTotalPedido * 0.01;
        }
        pedido.setComissao(comissao);

        // 7. Salva o pedido finalizado no banco local de Vendas
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 8. COMUNICAÇÃO SÍNCRONA (VERSÃO 1): Chama o serviço de Notificações (Novo!)
        integracaoService.enviarNotificacaoVenda(
                pedidoSalvo.getId(),
                pedidoSalvo.getTotal(),
                pedidoSalvo.getCpfCliente(),
                contemControlado);

        return pedidoSalvo;
    }

    // Método para listar todas as vendas (usado pelo Microsserviço de Relatórios)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }
}