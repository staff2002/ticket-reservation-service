package com.flyhigh.reservation.order.service.exception;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException() {
        super("order not found");
    }
}
