package com.example.vendas.dto;

// Classe simples apenas para receber os dados do MS Clientes
public class ClienteDTO {
    private String cpf;
    private String nome;
    private boolean idoso;
    private boolean possuiConvenio;

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public boolean isIdoso() { return idoso; }
    public void setIdoso(boolean idoso) { this.idoso = idoso; }
    public boolean isPossuiConvenio() { return possuiConvenio; }
    public void setPossuiConvenio(boolean possuiConvenio) { this.possuiConvenio = possuiConvenio; }
}