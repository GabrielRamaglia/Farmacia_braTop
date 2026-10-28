package com.example.farmacia.controller;
import org.springframework.web.bind.annotation.*;
import com.example.farmacia.model.Pedido;
import com.example.farmacia.service.PedidoService;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public String criarPedido(@RequestBody Pedido pedido) {
        return service.processarPedido(pedido);
    }
}