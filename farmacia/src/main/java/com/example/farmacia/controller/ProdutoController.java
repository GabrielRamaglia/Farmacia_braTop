package com.example.farmacia.controller;

import com.example.farmacia.model.Produto;
import com.example.farmacia.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

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
        if (produto.getQuantidadeEstoque() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O estoque inicial não pode ser negativo.");
        }
        produto.setId(null);
        return repository.save(produto);
    }

    @PatchMapping("/{id}/estoque")
    @Transactional
    public Produto reporEstoque(@PathVariable Long id, @RequestBody ReposicaoEstoque reposicao) {
        if (reposicao.quantidade() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quantidade de reposição deve ser maior que zero.");
        }

        Produto produto = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado."));
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + reposicao.quantidade());
        return repository.save(produto);
    }

    public record ReposicaoEstoque(int quantidade) {
    }
}
