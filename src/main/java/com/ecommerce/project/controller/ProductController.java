package com.ecommerce.project.controller;


import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;


    @GetMapping("/public/products")
    public  ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam (name = "pageSize",defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam (name = "sortBy",defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam (name = "sortOrder",defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
    ProductResponse productResponse = productService.getAllProducts(pageSize,sortBy,sortOrder);
    return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public  ResponseEntity<ProductResponse> getAllProductsByCategory( @PathVariable Long categoryId,
            @RequestParam (name = "pageSize",defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam (name = "sortBy",defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam (name = "sortOrder",defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
    ProductResponse productResponse = productService.getAllProductsByCategory(categoryId,pageSize,sortBy,sortOrder);
    return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @GetMapping("/public/products/keyword/{keyword}")
    public  ResponseEntity<ProductResponse> getAllProductsByKeyword( @PathVariable String keyword,
            @RequestParam (name = "pageSize",defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam (name = "sortBy",defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam (name = "sortOrder",defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder){
    ProductResponse productResponse = productService.getAllProductsByKeyword(keyword,pageSize,sortBy,sortOrder);
    return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }


    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO ,
                                                 @PathVariable Long categoryId){
        ProductDTO savedProductDTO = productService.addProduct(productDTO,categoryId);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO ,
                                                 @PathVariable Long productId){
        ProductDTO savedProductDTO = productService.updateProduct(productDTO,productId);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.OK);
    }
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO productDTO=productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image")MultipartFile image) throws IOException {
        ProductDTO productDTO = productService.updateProductImage(productId,image);
        return ResponseEntity.ok(productDTO);
    }
}
