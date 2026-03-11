package com.pagosservice.pagosservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.pagosservice.pagosservice.dto.UpdateOrderStatusRequest;

@FeignClient(name = "ordenes-service")
public interface OrderClient {

    @PutMapping("/ordenes/{id}/status")
    void updateOrderStatus(@PathVariable("id") String ordenId, @RequestBody UpdateOrderStatusRequest request);
}
