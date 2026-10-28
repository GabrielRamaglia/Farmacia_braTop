package com.example.farmacia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produtos")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private double precoBase;
    private TipoProduto tipoProduto; // MEDICAMENTO, HIGIENE, CONTROLADO

    // Getters, Setters e Construtores



    /**
     * @return Long return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return String return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return double return the precoBase
     */
    public double getPrecoBase() {
        return precoBase;
    }

    /**
     * @param precoBase the precoBase to set
     */
    public void setPrecoBase(double precoBase) {
        this.precoBase = precoBase;
    }

		public TipoProduto getTipoProduto() {
			return tipoProduto;
		}

}

