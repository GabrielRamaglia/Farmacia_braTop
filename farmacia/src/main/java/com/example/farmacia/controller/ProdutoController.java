package com.example.farmacia.controller;

import com.example.farmacia.model.Produto;
import com.example.farmacia.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository repository;

    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Produto> listar() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto cadastrar(@RequestBody Produto produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o nome do produto.");
        }
        if (produto.getPrecoBase() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O preço deve ser maior que zero.");
        }
        if (produto.getTipoProduto() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione o tipo do produto.");
        }
        produto.setId(null);
        return repository.save(produto);
    }
}
