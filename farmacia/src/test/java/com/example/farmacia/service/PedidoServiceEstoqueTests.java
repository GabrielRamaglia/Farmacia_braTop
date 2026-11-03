package com.example.farmacia.service;

import com.example.farmacia.adapter.FornecedorAdapter;
import com.example.farmacia.model.CanalVenda;
import com.example.farmacia.model.ItemPedido;
import com.example.farmacia.model.Pedido;
import com.example.farmacia.model.Produto;
import com.example.farmacia.model.TipoProduto;
import com.example.farmacia.repository.ClienteRepository;
import com.example.farmacia.repository.PedidoRepository;
import com.example.farmacia.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoServiceEstoqueTests {

    private PedidoRepository pedidoRepository;
    private ProdutoRepository produtoRepository;
    private SefazService sefaz;
    private AnsService ans;
    private FornecedorAdapter fornecedor;
    private PedidoService service;
    private Produto produto;

    @BeforeEach
    void setUp() {
        pedidoRepository = mock(PedidoRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        sefaz = mock(SefazService.class);
        ans = mock(AnsService.class);
        fornecedor = mock(FornecedorAdapter.class);
        service = new PedidoService(
                pedidoRepository, produtoRepository, clienteRepository, sefaz, ans, fornecedor);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto de teste");
        produto.setPrecoBase(10.0);
        produto.setTipoProduto(TipoProduto.MEDICAMENTO);
        produto.setQuantidadeEstoque(5);

        when(produtoRepository.findAll()).thenReturn(List.of(produto));
        when(produtoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void processarPedidoDeveBaixarQuantidadeVendidaDoEstoque() {
        Pedido pedido = criarPedido(3);

        service.processarPedido(pedido);

        assertEquals(2, produto.getQuantidadeEstoque());
        assertEquals(30.0, pedido.getValorTotal());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void processarPedidoDeveRejeitarQuantidadeMaiorQueEstoque() {
        Pedido pedido = criarPedido(6);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.processarPedido(pedido));

        assertEquals("Estoque insuficiente para Produto de teste. Disponível: 5.", exception.getMessage());
        assertEquals(5, produto.getQuantidadeEstoque());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    private Pedido criarPedido(int quantidade) {
        Produto produtoPedido = new Produto();
        produtoPedido.setId(1L);

        ItemPedido item = new ItemPedido();
        item.setProduto(produtoPedido);
        item.setQuantidade(quantidade);

        Pedido pedido = new Pedido();
        pedido.setCanalVenda(CanalVenda.BALCAO);
        pedido.setItens(List.of(item));
        return pedido;
    }
}
