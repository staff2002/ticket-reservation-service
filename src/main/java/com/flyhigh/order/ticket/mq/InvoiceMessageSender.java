package com.flyhigh.order.ticket.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyhigh.order.ticket.MQ.RabbitMqConfig;
import com.flyhigh.order.ticket.mq.messages.InvoiceMessage;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InvoiceMessageSender {

    private final ObjectMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    public boolean send(InvoiceMessage invoiceMessage) {
        try {
            String message = mapper.writeValueAsString(invoiceMessage);
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUNTING_KEY, message);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
