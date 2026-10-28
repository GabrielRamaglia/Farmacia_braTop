package com.example.farmacia.service;
import org.springframework.stereotype.Service;

import com.example.farmacia.external.Sefaz;

@Service
public class SefazService implements Sefaz {
    public void enviarNotaFiscal(String nota) {
        System.out.println("NF enviada para SEFAZ: " + nota);
    }
}
