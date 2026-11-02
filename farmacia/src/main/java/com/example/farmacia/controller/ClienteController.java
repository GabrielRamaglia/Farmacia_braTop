package com.example.farmacia.controller;

import com.example.farmacia.model.Cliente;
import com.example.farmacia.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Cliente> listar() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente cadastrar(@RequestBody Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o nome do cliente.");
        }
        if (cliente.getCpf() == null || cliente.getCpf().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o CPF do cliente.");
        }
        if (repository.findByCpf(cliente.getCpf()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente com esse CPF.");
        }
        cliente.setId(null);
        return repository.save(cliente);
    }
}
