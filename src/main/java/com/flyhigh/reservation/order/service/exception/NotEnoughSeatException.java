package com.flyhigh.reservation.order.service.exception;

public class NotEnoughSeatException extends RuntimeException{
    public NotEnoughSeatException() {
        super("not enough seat");
    }
}
