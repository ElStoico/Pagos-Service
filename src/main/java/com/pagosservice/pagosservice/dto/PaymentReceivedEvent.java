package com.pagosservice.pagosservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReceivedEvent {
    private String orderId;
    private String usuarioId;
    private Double amount;
    private String paymentMethod;
    private String transactionId;
    private Long timestamp;
}
