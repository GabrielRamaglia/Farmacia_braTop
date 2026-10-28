package com.example.clientes.controller;

import com.example.clientes.model.Cliente;
import com.example.clientes.repository.ClienteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // <-- O IMPORT QUE FALTAVA!
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    // 1. LISTAR TODOS OS CLIENTES
    @GetMapping
    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    // 2. BUSCAR CLIENTE PELO CPF
    @GetMapping("/{cpf}")
    public ResponseEntity<Cliente> buscarPorCpf(@PathVariable String cpf) {
        Optional<Cliente> cliente = repository.findByCpf(cpf);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.notFound().build();
    }

    // 3. CADASTRAR UM NOVO CLIENTE
    @PostMapping
    @Transactional // <-- AGORA VAI FUNCIONAR E FORÇAR A GRAVAÇÃO NO BANCO
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody Cliente cliente) {
        // Salva e força o flush (gravação imediata) no banco
        Cliente clienteSalvo = repository.saveAndFlush(cliente);
        return ResponseEntity.ok(clienteSalvo);
    }

    // 4. ATUALIZAR UM CLIENTE EXISTENTE
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        Optional<Cliente> clienteExistente = repository.findById(id);

        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();
            cliente.setNome(clienteAtualizado.getNome());
            cliente.setCpf(clienteAtualizado.getCpf());
            cliente.setIdoso(clienteAtualizado.isIdoso());
            cliente.setPossuiConvenio(clienteAtualizado.isPossuiConvenio());

            repository.save(cliente);
            return ResponseEntity.ok(cliente);
        }
        return ResponseEntity.notFound().build();
    }

    // 5. DELETAR UM CLIENTE
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> apagarCliente(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}