package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // create our own query and let Spring execute it
    @Query("SELECT c from Cart c WHERE c.user.email = ?1")
    Cart findCartByEmail(String email);

    @Query("SELECT c from Cart c WHERE c.user.email = ?1 And c.id = ?2")
    Cart findCartByEmailAndCartId(String email, Long cartId);

    @Query("SELECT cart FROM Cart cart JOIN FETCH cart.cartItems cart_item JOIN FETCH cart_item.product product WHERE product.id = ?1")
    List<Cart> findCartsByProductId(Long productId);
}
