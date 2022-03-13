package com.flyhigh.reservation.order.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyhigh.reservation.order.mq.messages.InvoiceMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceMessageSenderTest {

    @Test
    public void should_return_true_given_send_message_success() {

        RabbitTemplate stuRabbitTemplate = Mockito.mock(RabbitTemplate.class);
        ObjectMapper objectMapper = new ObjectMapper();
        InvoiceMessageSender invoiceMessageSender = new InvoiceMessageSender(objectMapper, stuRabbitTemplate);

        boolean result = invoiceMessageSender.send(InvoiceMessage.builder()
                        .invoiceTitle("李四")
                        .amount(BigDecimal.valueOf(3500L))
                        .email("lisi@163.com").build());

        assertTrue(result);
    }
}
