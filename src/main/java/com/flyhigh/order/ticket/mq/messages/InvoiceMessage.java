package com.flyhigh.order.ticket.mq.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceMessage {
    private String invoiceTitle;
    private BigDecimal amount;
    private String email;
}

