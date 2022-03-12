package com.flyhigh.order.ticket.service.exception;

public class MessageServiceNotAvailableException extends RuntimeException{
    public MessageServiceNotAvailableException() {
        super("Message Service Not available");
    }
}
