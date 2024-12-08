package com.ecommerce.project.controller.address;

import com.ecommerce.project.payload.address.AddressDTO;
import com.ecommerce.project.service.address.AddressService;
import com.ecommerce.project.util.Constants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.FULL_API)
public class AddressController {

    @Autowired
    AddressService addressService;

    @PostMapping(value = "/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping(value = "/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses() {
        List<AddressDTO> savedAddressDTOS = addressService.getAddresses();
        return new ResponseEntity<>(savedAddressDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        AddressDTO savedAddress = addressService.getAddressById(addressId);
        return new ResponseEntity<>(savedAddress, HttpStatus.OK);
    }

    @GetMapping(value = "users/addresses/")
    public ResponseEntity<List<AddressDTO>> getAddressByUser() {
        List<AddressDTO> savedAddresses = addressService.getAddressByUsers();
        return new ResponseEntity<>(savedAddresses, HttpStatus.OK);
    }

    @PutMapping(value = "/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO) {
        AddressDTO savedAddress = addressService.updateAddress(addressId, addressDTO);
        return new ResponseEntity<>(savedAddress, HttpStatus.OK);
    }

    @DeleteMapping(value = "/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
