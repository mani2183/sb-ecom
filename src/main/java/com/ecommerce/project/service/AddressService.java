package com.ecommerce.project.service;

import com.ecommerce.project.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    List<AddressDTO> getAllAddresses();

    AddressDTO addAddress(AddressDTO addressDTO);

    List<AddressDTO> getAllAddressesByUser();

    AddressDTO getAddressById(Long addressId);

    AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO);

    String deleteAddressById(Long addressId);
}
