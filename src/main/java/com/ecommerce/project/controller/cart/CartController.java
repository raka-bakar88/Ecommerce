package com.ecommerce.project.controller.cart;

import com.ecommerce.project.payload.cart.CartDTO;
import com.ecommerce.project.service.cart.CartService;
import com.ecommerce.project.util.AuthUtil;
import com.ecommerce.project.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.FULL_API)
public class CartController {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartService cartService;

    /**
     * add a product to a cart
     *
     * @param productId ID of the product
     * @param quantity  quantity of the product that the customer want to buy
     * @return ResponseEntity<CartDTO>
     */
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity
    ) {

        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    /**
     * get all carts
     *
     * @return ResponseEntity<List < CartDTO>>
     */
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts() {
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOS, HttpStatus.FOUND);
    }

    /**
     * get cart data by email and cartId
     * it anticipates for system scaling where if a user can have multiple carts
     *
     * @return ResponseEntity<CartDTO>
     */
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById() {
        CartDTO cartDTO = cartService.getCart();
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation) {
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId) {
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
