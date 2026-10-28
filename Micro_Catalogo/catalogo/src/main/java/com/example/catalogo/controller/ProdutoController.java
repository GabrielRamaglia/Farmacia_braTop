package com.example.catalogo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.catalogo.model.Produto;
import com.example.catalogo.repository.ProdutoRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository repository;

    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    // 1. LISTAR TODOS OS PRODUTOS (GET)
    @GetMapping
    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    // 2. BUSCAR PRODUTO POR ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Optional<Produto> produto = repository.findById(id);
        if (produto.isPresent()) {
            return ResponseEntity.ok(produto.get());
        }
        return ResponseEntity.notFound().build();
    }

    // 3. CRIAR NOVO PRODUTO (POST)
    @PostMapping
    public Produto criarProduto(@RequestBody Produto produto) {
        return repository.save(produto);
    }

    // 4. ATUALIZAR PRODUTO EXISTENTE (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id, @RequestBody Produto produtoAtualizado) {
        Optional<Produto> produtoExistente = repository.findById(id);
        
        if (produtoExistente.isPresent()) {
            Produto produto = produtoExistente.get();
            produto.setNome(produtoAtualizado.getNome());
            produto.setPrecoBase(produtoAtualizado.getPrecoBase());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produto.setTipoProduto(produtoAtualizado.getTipoProduto());
            
            repository.save(produto);
            return ResponseEntity.ok(produto);
        }
        return ResponseEntity.notFound().build();
    }

    // 5. ELIMINAR PRODUTO (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarProduto(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}