package com.ecommerce.project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
 @Id
 @GeneratedValue(strategy = GenerationType.AUTO)
 private Long productId;
 @NotBlank
 @Size(min = 3,message = "Product name must contain atleast  characters")
 private String productName;
 private String description;
 private String image;
 private Integer quantity;
 private  Double price;
 private  Double specialPrice;
 private Double discount;

 @ManyToOne
 @JoinColumn(name = "category_id")
 private Category category;






}
