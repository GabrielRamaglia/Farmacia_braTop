package com.example.farmacia.adapter;
// adapter/FornecedorAAdapter.java
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FornecedorAAdapter implements FornecedorAdapter {

    private final RestTemplate restTemplate = new RestTemplate();

    public String enviarPedido(String pedido) {
        return restTemplate.postForObject(
            "http://localhost:8081/pedido",
            pedido,
            String.class
        );
    }
}