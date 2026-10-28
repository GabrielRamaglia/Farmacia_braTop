package com.example.fornecedores.controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class FornecedorController {

    @PostMapping("/pedido")
    public String processarPedido(@RequestBody String pedido) {

        System.out.println("Recebido pedido: " + pedido);

        // simulação COBOL (arquivo)
        return "Fornecedor A processou pedido com sucesso";
    }
}