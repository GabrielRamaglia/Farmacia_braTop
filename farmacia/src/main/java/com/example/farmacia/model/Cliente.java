package com.example.farmacia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    
    @Column(unique = true)
    private String cpf;
    
    private boolean isIdoso;
    private boolean possuiConvenio;
    private boolean isCadastrado; // Para validar se tem direito às bonificações progressivas

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
     * @return String return the cpf
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * @param cpf the cpf to set
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * @return boolean return the isIdoso
     */
    public boolean isIdoso() {
        return isIdoso;
    }

    /**
     * @param isIdoso the isIdoso to set
     */
    public void setisIdoso(boolean isIdoso) {
        this.isIdoso = isIdoso;
    }

    /**
     * @return boolean return the possuiConvenio
     */
    public boolean isPossuiConvenio() {
        return possuiConvenio;
    }

    /**
     * @param possuiConvenio the possuiConvenio to set
     */
    public void setPossuiConvenio(boolean possuiConvenio) {
        this.possuiConvenio = possuiConvenio;
    }

    public boolean getisCadastrado(){ 
        return isCadastrado;
}

  
}