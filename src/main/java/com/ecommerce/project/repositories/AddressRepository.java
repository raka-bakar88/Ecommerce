package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a WHERE a.user = ?1")
    List<Address> findAddressByUser(User user);

    @Query("SELECT a FROM Address a WHERE a.id = ?1")
    Address findAddressById(Long id);
}
