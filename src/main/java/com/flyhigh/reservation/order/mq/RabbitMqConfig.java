package com.flyhigh.reservation.order.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {


    public static final String EXCHANGE_NAME = "order-flight-exchange";

    public static final String ROUNTING_KEY = "order-flight-rounting-key";

    public static final String QUEUE_NAME = "order-flight-queue";


    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue orderTicketQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(orderTicketQueue()).to(directExchange()).with(ROUNTING_KEY);
    }

}
