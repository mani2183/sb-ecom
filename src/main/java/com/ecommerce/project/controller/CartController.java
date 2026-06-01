package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity){
        CartDTO cartDTO = cartService.addProductToCart(productId,quantity);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> cartDTOS = cartService.getAllCarts();

        return ResponseEntity.ok(cartDTOS);
    }

    @GetMapping("/users/cart")
    public ResponseEntity<CartDTO> getUserCart(){
        return ResponseEntity.ok(cartService.getUserCart());
    }

    @PutMapping("/products/{productId}/product/{quantity}")
    public ResponseEntity<CartDTO> updateQuantityInCart(@PathVariable Long productId,
                                                        @PathVariable Integer quantity){
        CartDTO cartDTO = cartService.updateQuantityInCart(productId,quantity);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/product/{productId}/{operation}")
    public ResponseEntity<CartDTO> updateProductQuantityInCart(@PathVariable Long productId,
                                                        @PathVariable String operation) {
        int delete = operation.equalsIgnoreCase("delete") ? -1 : 1;
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, delete);

        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}/product/{productId}")
    public  ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                         @PathVariable Long productId){
        String response = cartService.deleteProductFromCart(cartId,productId);
        return ResponseEntity.ok("Product from your cart is deleted successfully");
    }
}
