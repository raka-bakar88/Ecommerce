package com.ecommerce.project.payload.order;

import com.ecommerce.project.payload.orderitem.OrderItemDTO;
import com.ecommerce.project.payload.payment.PaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Representing an Order, OrderDTO will be the class to be sent to the client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItems;
    private LocalDate orderDate;
    private PaymentDTO payment;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;
}
