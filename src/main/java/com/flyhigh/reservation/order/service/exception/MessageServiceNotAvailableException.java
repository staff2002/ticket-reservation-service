package com.flyhigh.reservation.order.service.exception;

public class MessageServiceNotAvailableException extends RuntimeException{
    public MessageServiceNotAvailableException() {
        super("Message Service Not available");
    }
}
