package com.pagosservice.pagosservice.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "pagos")
public class Payment {

    @Id
    private String id;
    private String ordenId;
    private Double monto;
    private String metodoPago;
    private LocalDateTime fecha;
    private String estado;
}
