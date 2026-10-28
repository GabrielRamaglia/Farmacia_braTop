package com.example.vendas.controller;

import com.example.vendas.model.Pedido;
import com.example.vendas.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> realizarVenda(@RequestBody Pedido pedido) {
        try {
            Pedido pedidoSalvo = pedidoService.processarVenda(pedido);
            return ResponseEntity.ok(pedidoSalvo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

// ADICIONE ESTE MÉTODO NO PedidoController.java (Microsserviço Vendas)
    @GetMapping
    public ResponseEntity<java.util.List<Pedido>> listarTodasAsVendas() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }
}