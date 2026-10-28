package com.example.farmacia.service;
import org.springframework.stereotype.Service;

import com.example.farmacia.external.ANS;

@Service
public class AnsService implements ANS {
    public void enviarReceita(String receita) {
        System.out.println("Receita enviada para ANS: " + receita);
    }
}