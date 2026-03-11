package com.pagosservice.pagosservice.controller;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pagosservice.pagosservice.client.OrderClient;
import com.pagosservice.pagosservice.dto.UpdateOrderStatusRequest;
import com.pagosservice.pagosservice.model.Payment;
import com.pagosservice.pagosservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private static final String ESTADO_PROCESADO = "PROCESADO";
    private static final String ESTADO_REEMBOLSADO = "REEMBOLSADO";
    private static final String ESTADO_ORDEN_PAGADA = "PAGADA";

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    @PostMapping("/procesar")
    @ResponseStatus(HttpStatus.CREATED)
    public Payment procesarPago(@RequestBody Payment request) {
        log.info("Procesando pago ordenId={} monto={} metodo={}",
            request.getOrdenId(), request.getMonto(), request.getMetodoPago());
        Payment payment = Payment.builder()
                .ordenId(request.getOrdenId())
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .fecha(LocalDateTime.now())
                .estado(ESTADO_PROCESADO)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        try {
            orderClient.updateOrderStatus(savedPayment.getOrdenId(), new UpdateOrderStatusRequest(ESTADO_ORDEN_PAGADA));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Pago registrado, pero no se pudo actualizar el estado de la orden", ex);
        }

        return savedPayment;
    }

    @GetMapping("/{id}")
    public Payment getPagoById(@PathVariable String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado: " + id));
    }

    @GetMapping("/orden/{id}")
    public Payment getPagoByOrden(@PathVariable("id") String ordenId) {
        return paymentRepository.findByOrdenId(ordenId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado para orden: " + ordenId));
    }

    @PutMapping("/{id}/reembolso")
    public Payment reembolsarPago(@PathVariable String id) {
        try {
            Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Pago no encontrado: " + id));

            payment.setEstado(ESTADO_REEMBOLSADO);
            return paymentRepository.save(payment);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }
}
