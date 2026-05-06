package com.pagosservice.pagosservice.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.pagosservice.pagosservice.dto.PaymentReceivedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReceivedEventProducer {

    private final KafkaTemplate<String, PaymentReceivedEvent> kafkaTemplate;

    @Value("${app.topic.payment-received}")
    private String paymentReceivedTopic;

    public void publishPaymentReceived(String orderId, String usuarioId, Double amount, 
                                       String paymentMethod, String transactionId) {
        log.info("[PRODUCER-PAYMENT] ===== START: Publishing payment received event for order: {} =====", orderId);
        log.debug("[PRODUCER-PAYMENT] Payment details - Amount: {}, Method: {}", amount, paymentMethod);
        
        PaymentReceivedEvent event = PaymentReceivedEvent.builder()
                .orderId(orderId)
                .usuarioId(usuarioId)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .transactionId(transactionId)
                .timestamp(System.currentTimeMillis())
                .build();

        log.info("[PRODUCER-PAYMENT] Event created: {}", event);
        log.info("[PRODUCER-PAYMENT] Topic: {}", paymentReceivedTopic);

        try {
            log.info("[PRODUCER-PAYMENT] Sending message to Kafka...");
            var sendResult = kafkaTemplate.send(paymentReceivedTopic, orderId, event);
            var completableFuture = sendResult.toCompletableFuture();
            
            completableFuture.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("[PRODUCER-PAYMENT] ✓ Message sent successfully! Partition: {}, Offset: {}", 
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("[PRODUCER-PAYMENT] ✗ Failed to send message: {}", ex.getMessage(), ex);
                }
            });
            
            log.info("[PRODUCER-PAYMENT] ===== END: Payment event publishing initiated =====");
        } catch (Exception ex) {
            log.error("[PRODUCER-PAYMENT] Exception during send: {}", ex.getMessage(), ex);
            throw new IllegalStateException("No se pudo publicar el evento de pago para la orden " + orderId, ex);
        }
    }
}
