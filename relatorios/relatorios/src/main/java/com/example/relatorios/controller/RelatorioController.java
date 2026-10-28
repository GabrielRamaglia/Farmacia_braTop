package com.example.relatorios.controller;

import com.example.relatorios.dto.ModelosDTO.*;
import com.example.relatorios.service.RelatorioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping("/estoque-baixo")
    public List<ProdutoDTO> getEstoqueBaixo() {
        return service.relatorioEstoqueBaixo();
    }

    @GetMapping("/vendas-geral")
    public List<PedidoDTO> getVendasGeral() {
        return service.relatorioTodasVendas();
    }

    @GetMapping("/mais-vendidos")
    public List<ProdutoMaisVendidoDTO> getMaisVendidos() {
        return service.relatorioMaisVendidos();
    }
}