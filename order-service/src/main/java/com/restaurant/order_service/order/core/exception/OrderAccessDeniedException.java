package com.restaurant.order_service.order.core.exception;

public class OrderAccessDeniedException extends RuntimeException {
    public OrderAccessDeniedException() {
        super("Order does not belong to the authenticated client");
    }
}
