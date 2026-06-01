package com.ecommerce.project.controller;

import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses(){
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addAddress(@RequestBody AddressDTO addressDTO){
        return new ResponseEntity<>(addressService.addAddress(addressDTO), HttpStatus.CREATED);
    }
    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddressesByUser(){
        return ResponseEntity.ok(addressService.getAllAddressesByUser());
    }
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
        return ResponseEntity.ok(addressService.getAddressById(addressId));
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId, @Valid @RequestBody AddressDTO addressDTO){
        return ResponseEntity.ok(addressService.updateAddressById(addressId,addressDTO));
    }
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId){
        return ResponseEntity.ok(addressService.deleteAddressById(addressId));
    }

}
