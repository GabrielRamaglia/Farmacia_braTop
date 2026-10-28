package com.example.vendas.configRabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_VENDAS = "vendas.exchange";
    public static final String ROUTING_KEY_VENDA_REALIZADA = "venda.realizada";
    public static final String FILA_NOTIFICACOES = "vendas.notificacoes.queue";

    // 1. Cria a Fila
    @Bean
    public Queue filaNotificacoes() {
        return new Queue(FILA_NOTIFICACOES, true); 
    }

    // 2. Cria a Exchange (O "Roteador" de mensagens)
    @Bean
    public DirectExchange exchangeVendas() {
        return new DirectExchange(EXCHANGE_VENDAS);
    }

    // 3. Liga a Fila à Exchange
    @Bean
    public Binding binding(Queue filaNotificacoes, DirectExchange exchangeVendas) {
        return BindingBuilder.bind(filaNotificacoes).to(exchangeVendas).with(ROUTING_KEY_VENDA_REALIZADA);
    }

    // 4. O NOSSO TRADUTOR! (Converte Java para JSON)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 5. Força o carteiro (RabbitTemplate) a usar o nosso tradutor JSON
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}