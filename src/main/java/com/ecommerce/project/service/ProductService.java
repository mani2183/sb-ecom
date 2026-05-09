package com.ecommerce.project.service;


import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(Product product, Long categortId);

    ProductResponse getAllProducts(Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getAllProductsByCategory(Long categoryId, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getAllProductsByKeyword(String keyword, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(Product product, Long productId);

    ProductDTO deleteProduct(Long productId);
}
