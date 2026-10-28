package com.example.farmacia.service;

import com.example.farmacia.adapter.FornecedorAdapter;
import com.example.farmacia.model.*;
import com.example.farmacia.repository.PedidoRepository;
import com.example.farmacia.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final SefazService sefaz;
    private final AnsService ans;
    private final FornecedorAdapter fornecedor;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         SefazService sefaz,
                         AnsService ans,
                         FornecedorAdapter fornecedor) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.sefaz = sefaz;
        this.ans = ans;
        this.fornecedor = fornecedor;
    }

    @Transactional
    public String processarPedido(Pedido pedido) {
        double valorTotalPedido = 0.0;
        boolean contemControlado = false;
        System.out.println("TOTAL DE PRODUTOS NO BANCO: " + produtoRepository.findAll().size());

        // 1. Processar cada item do pedido e aplicar as regras de preço e desconto
        for (ItemPedido item : pedido.getItens()) {
            // Busca o produto completo no banco de dados para garantir os valores e tipos reais
            Optional<Produto> produtoDb = produtoRepository.findById(item.getProduto().getId());
            if (produtoDb.isEmpty()) {
                throw new RuntimeException("Produto com ID " + item.getProduto().getId() + " não encontrado.");
            }
            
            Produto produto = produtoDb.get();
            item.setProduto(produto);
            item.setPedido(pedido);

            if (produto.getTipoProduto() == TipoProduto.CONTROLADO) {
                contemControlado = true;
            }

            // Cálculo do preço praticado com base nas regras de desconto
            double precoFinalUnitario = produto.getPrecoBase();
            Cliente cliente = pedido.getCliente();

            if (cliente != null) {
                // Regra: Idoso + Convênio (Desconto aplicável apenas para medicamentos)
                if (cliente.isIdoso() && cliente.isPossuiConvenio() && 
                   (produto.getTipoProduto() == TipoProduto.MEDICAMENTO || produto.getTipoProduto() == TipoProduto.CONTROLADO)) {
                    precoFinalUnitario = precoFinalUnitario * 0.80; // 20% de desconto
                } 
                // Regra: Cliente cadastrado comum (Bonificação/Desconto progressivo de 10%)
                else if (cliente.getisCadastrado()) {
                    precoFinalUnitario = precoFinalUnitario * 0.90; // 10% de desconto
                }
            }

            item.setPrecoPraticado(precoFinalUnitario);
            valorTotalPedido += (precoFinalUnitario * item.getQuantidade());
        }

        // 2. Validação obrigatória do PDF: Medicação controlada EXIGE dados do cliente
        if (contemControlado && (pedido.getCliente() == null || pedido.getCliente().getCpf() == null)) {
            throw new RuntimeException("Venda bloqueada: Medicamentos controlados exigem a coleta dos dados do cliente.");
        }

        pedido.setValorTotal(valorTotalPedido);

        // 3. Regra: Lançar comissão de 5% para o vendedor se a venda for no balcão
        if (pedido.getCanalVenda() == CanalVenda.BALCAO && pedido.getVendedorId() != null) {
            double comissao = valorTotalPedido * 0.05;
            pedido.setComissaoGerada(comissao);
            System.out.println("Comissão de R$ " + comissao + " gerada para o vendedor ID: " + pedido.getVendedorId());
        } else {
            pedido.setComissaoGerada(0.0);
        }

        // 4. Salvar o pedido completo com seus itens de forma persistente no MySQL
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 5. Acionar as integrações externas obrigatórias usando os dados reais salvos
        String notaFiscal = "NF-e ID: " + pedidoSalvo.getId() + " | CPF: " + (pedidoSalvo.getCpfNaNota() != null ? pedidoSalvo.getCpfNaNota() : "Não informado") + " | Total: R$ " + pedidoSalvo.getValorTotal();
        sefaz.enviarNotaFiscal(notaFiscal);

        if (contemControlado) {
            ans.enviarReceita("Notificação de Receita Retida - Pedido ID: " + pedidoSalvo.getId() + " | Cliente CPF: " + pedidoSalvo.getCliente().getCpf());
        }

        // Envia o primeiro produto para a API de fornecedores como exemplo de comunicação síncrona (Versão 1)
        if (!pedidoSalvo.getItens().isEmpty()) {
            fornecedor.enviarPedido("Pedido de Reposição - Produto: " + pedidoSalvo.getItens().get(0).getProduto().getNome());
        }

        return "Pedido ID " + pedidoSalvo.getId() + " processado e salvo com sucesso no MySQL!";
    }

}