package com.example.farmacia.repository;

import com.example.farmacia.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select produto from Produto produto where produto.id = :id")
    Optional<Produto> findByIdForUpdate(@Param("id") Long id);
}