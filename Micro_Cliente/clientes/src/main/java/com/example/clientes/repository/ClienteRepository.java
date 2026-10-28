package com.example.clientes.repository;

import com.example.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Método personalizado super importante para a Farmácia buscar os dados usando apenas o CPF
    Optional<Cliente> findByCpf(String cpf);
    
}
