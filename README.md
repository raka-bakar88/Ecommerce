This is an Ecommerce Backend application that provides a API for online shopping. 
It provides APIs CRUD operation and more, for instance for managing  category product, User, Cart, Authentication etc.
It uses Java Web Token for authentication and security management.

Tech stacks:
- Spring Boot(Java 17)
- MySql database
- Json Web Token(JWT)
- Java Persistence Api (JPA)
- Lombok
- Model Mapper
- Spring Framework Security

  **Architecture design**

  It uses three layer designs which is following best practises, various principles(eg: Separation of concern, SOLID princicle) etc.
- Controller layer = as a gateway to accept api request from clients and returns response from the server back to the client.
- Service layer = where the business logic is being managed
- Repository layer = to access data resource such as Database. In this application, it uses MySQL Database
  This application provides 6 groups of Restful-Api that can be consumed by the clients.
  
  ![alt text](https://github.com/raka-bakar88/Ecommerce/blob/main/design%20structure.png)

  **Database**

  This application is using MySQL database. Below is the Entity Relationship Diagram the database
     ![alt text](https://github.com/raka-bakar88/Ecommerce/blob/main/ECom%20ER%20DIagram.png)

   **API endpoints**

  Below is the list of api endpoints :
  Product API
  ![alt text](https://github.com/raka-bakar88/Ecommerce/blob/main/Product%20API%20Endpoint.png)
  Category API
  ![alt text](https://github.com/raka-bakar88/Ecommerce/blob/main/Category%20API%20Endpoints.png)
  Authentication API
  ![alt text](https://github.com/raka-bakar88/Ecommerce/blob/main/Authentication%20API%20Endpoints.png)
  Cart API
  ![alt text](https://github.com/raka-bakar88/Ecommerce/blob/main/Cart%20API%20Endpoints.png)
  Addresses API
  ![alt text](https://github.com/raka-bakar88/Ecommerce/blob/main/Addresses%20API%20endpoints.png)

**Example Class**


  Below is an example of code from Class OrderServiceImpl
   ````
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

    ````

  
