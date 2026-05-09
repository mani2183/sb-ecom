package com.ecommerce.project.service;


import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.ResourceNotFountException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements  ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFountException("Category","categoryId", categoryId));
        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice = product.getPrice()*((100- product.getDiscount())*0.01);
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);



    }

    @Override
    public ProductResponse getAllProducts(Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(0,pageSize,sortByAndOrder);
        Page<Product> productpage = productRepository.findAll(pageable);
        List<Product> productList=productpage.getContent();
        if (productList.isEmpty()){
            throw new ApiException("No products is added till now");
        }
        List<ProductDTO> productDTOS = productList.stream().map(product-> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse =new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productpage.getNumber());
        productResponse.setPageSize(productpage.getSize());
        productResponse.setTotalElements(productpage.getTotalElements());
        productResponse.setTotalPages(productpage.getTotalPages());
        productResponse.setLastPage(productpage.isLast());

        return productResponse;



    }

    @Override
    public ProductResponse getAllProductsByCategory(Long categoryId, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFountException("Category","categoryId",categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(0,pageSize,sortByAndOrder);
        Page<Product> productpage = productRepository.findByCategory(category,pageable);
        List<Product> productList=productpage.getContent();
        if (productList.isEmpty()){
            throw new ApiException("No products is added till now");
        }
        List<ProductDTO> productDTOS = productList.stream().map(product-> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse =new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productpage.getNumber());
        productResponse.setPageSize(productpage.getSize());
        productResponse.setTotalElements(productpage.getTotalElements());
        productResponse.setTotalPages(productpage.getTotalPages());
        productResponse.setLastPage(productpage.isLast());

        return productResponse;

    }

    @Override
    public ProductResponse getAllProductsByKeyword(String keyword, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(0,pageSize,sortByAndOrder);
        Page<Product> productpage = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageable);
        List<Product> productList=productpage.getContent();
        if (productList.isEmpty()){
            throw new ApiException("No products found with keyword: "+keyword);
        }
        List<ProductDTO> productDTOS = productList.stream().map(product-> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse =new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productpage.getNumber());
        productResponse.setPageSize(productpage.getSize());
        productResponse.setTotalElements(productpage.getTotalElements());
        productResponse.setTotalPages(productpage.getTotalPages());
        productResponse.setLastPage(productpage.isLast());

        return productResponse;

    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product savedProduct = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFountException("Product","productId",productId));
        Product product = modelMapper.map(productDTO, Product.class);
        product.setProductId(savedProduct.getProductId());
        product.setSpecialPrice(product.getPrice()*((100- product.getDiscount())*0.01));
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ApiException("Product with productId:"+productId+" not found"));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product =productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFountException("Product","productId",productId));
        String path = "images/";
        String fileName = uploadImage(path,image);
        product.setImage(fileName);
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    private String uploadImage(String path, MultipartFile image) throws IOException {
        String originalFileName = image.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName
                .substring(originalFileName.lastIndexOf(".")));
        String filePath = path + File.separator+fileName;
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }
        Files.copy(image.getInputStream(), Paths.get(filePath));
        return fileName;
    }
}
