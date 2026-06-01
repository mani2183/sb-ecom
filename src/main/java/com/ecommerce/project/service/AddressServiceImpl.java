package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements  AddressService{

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<AddressDTO> getAllAddresses() {
        if(authUtil.isAdmin()){
            List<Address> addresses = addressRepository.findAll();
            if(addresses .isEmpty())
                throw new ApiException("no addresses added till now");
            return addresses.stream().map(address ->
                    modelMapper.map(address,AddressDTO.class)
                    ).toList();
        }else{
            throw new ApiException("You don't have privilege roles to perform this action");

        }
    }

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO) {
        User user =  authUtil.loggedInUser();
        Address address = modelMapper.map(addressDTO,Address.class);

        List<Address> addresses = user.getAddresses();
        addresses.add(address);
        user.setAddresses(addresses);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddressesByUser() {
        Long userId = authUtil.loggedInUserId();
        List<Address> addresses = addressRepository.findAllByUserId(userId);
        if(addresses.isEmpty())
            throw new ApiException("no addresses added till now for the user");
        return addresses.stream().map(address ->
                modelMapper.map(address,AddressDTO.class)
        ).toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address","addressId",addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {
        Address addressDb = addressRepository.findById(addressId)
                .orElseThrow( () ->new ResourceNotFoundException("Address","addressId",addressId));
        addressDb.setCity(addressDTO.getCity());
        addressDb.setState(addressDTO.getState());
        addressDb.setPincode(addressDTO.getPincode());
        addressDb.setCountry(addressDTO.getCountry());
        addressDb.setStreet(addressDTO.getStreet());
        addressDb.setBuildingName(addressDTO.getBuildingName());
        Address savedAddress = addressRepository.save(addressDb);
        User user = addressDb.getUser();
        user.getAddresses().removeIf(address ->address.getAddressId().equals(addressId));
        user.getAddresses().add(savedAddress);
        userRepository.save(user);
        return  modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        Address addressDb = addressRepository.findById(addressId)
                .orElseThrow( () ->new ResourceNotFoundException("Address","addressId",addressId));

        User user = addressDb.getUser();
        user.getAddresses().removeIf(address ->address.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepository.delete(addressDb);

        return "Address deleted successfully";
    }
}
