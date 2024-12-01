package com.ecommerce.project.service.order;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.order.OrderDTO;
import com.ecommerce.project.payload.orderitem.OrderItemDTO;
import com.ecommerce.project.repositories.*;
import com.ecommerce.project.service.cart.CartService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProductRepository productRepository;

    @Transactional
    @Override
    public OrderDTO placeOrder(String emailId,
                               Long addressId,
                               String paymentMethod,
                               String pgPaymentId,
                               String pgName,
                               String pgStatus,
                               String pgResponseMessage) {
        // get a cart that linked to the order
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null){
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }
        // get the address of the order
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        // Create Order instance
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");
        order.setAddress(address);

        // Create Payment instance and save it into the DB
        Payment payment = new Payment(paymentMethod,
                pgPaymentId,
                pgStatus,
                pgResponseMessage,
                pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        // Set order with a payment(1-to-1)
        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()){
            throw new APIException("Cart is empty");
        }

        // Create a list of orderitems based on all items in a cart and save it into DB
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);
        cart.getCartItems().forEach(item-> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();

            // Reducing the stock quantity
            product.setQuantity(product.getQuantity() - quantity);

            // save the updated quantity of a product into the DB
            productRepository.save(product);

            // remove item from cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
