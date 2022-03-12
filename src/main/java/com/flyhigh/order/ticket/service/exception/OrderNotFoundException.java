package com.flyhigh.order.ticket.service.exception;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException() {
        super("order not found");
    }
}
