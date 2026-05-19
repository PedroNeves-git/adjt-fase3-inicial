package com.restaurant.order_service.order.infra.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentPendingEvent(
        UUID eventId,
        String eventType,
        Instant timestamp,
        UUID orderId,
        BigDecimal totalAmount,
        String reason,
        Integer attempt
) {

    public static final String EVENT_TYPE = "PAYMENT_PENDING";
}
