package com.flyhigh.order.ticket.service.exception;

public class NotEnoughSeatException extends RuntimeException{
    public NotEnoughSeatException() {
        super("not enough seat");
    }
}
