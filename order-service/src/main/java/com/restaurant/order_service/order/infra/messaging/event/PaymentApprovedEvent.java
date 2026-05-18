package com.restaurant.order_service.order.infra.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentApprovedEvent(
        UUID eventId,
        String eventType,
        Instant timestamp,
        UUID orderId,
        String transactionId,
        BigDecimal amountPaid
) {

    public static final String EVENT_TYPE = "PAYMENT_APPROVED";
}
