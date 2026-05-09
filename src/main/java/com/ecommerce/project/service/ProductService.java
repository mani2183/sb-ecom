package com.ecommerce.project.service;


import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(ProductDTO product, Long categortId);

    ProductResponse getAllProducts(Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getAllProductsByCategory(Long categoryId, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getAllProductsByKeyword(String keyword, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(ProductDTO product, Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
