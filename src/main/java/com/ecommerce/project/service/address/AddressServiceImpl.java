package com.ecommerce.project.service.address;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.address.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        User loggedInUser = authUtil.loggedInUser();

        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(loggedInUser);

        List<Address> addressList = loggedInUser.getAddresses();
        addressList.add(address);
        loggedInUser.setAddresses(addressList);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address savedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressByUsers() {
        User loggedInUser = authUtil.loggedInUser();
        List<Address> savedAddress = loggedInUser.getAddresses();
        if (savedAddress.isEmpty()) {
            throw new ResourceNotFoundException("User", "User", loggedInUser.getUserName());
        }

        return savedAddress.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address savedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));


        Address newAddress = modelMapper.map(addressDTO, Address.class);
        savedAddress.setCountry(newAddress.getCountry());
        savedAddress.setBuildingName(newAddress.getBuildingName());
        savedAddress.setState(newAddress.getState());
        savedAddress.setPincode(newAddress.getPincode());
        savedAddress.setCity(newAddress.getCity());
        savedAddress.setStreet(newAddress.getStreet());

        User user = savedAddress.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(savedAddress);
        userRepository.save(user);
        return modelMapper.map(addressRepository.save(savedAddress), AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address savedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));
        addressRepository.delete(savedAddress);

        User user = savedAddress.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        return "Delete is successfull";
    }
}
