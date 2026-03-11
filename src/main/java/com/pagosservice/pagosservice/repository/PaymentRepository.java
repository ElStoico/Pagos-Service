package com.pagosservice.pagosservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pagosservice.pagosservice.model.Payment;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByOrdenId(String ordenId);
}
