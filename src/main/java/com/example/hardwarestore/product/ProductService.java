package com.example.hardwarestore.product;

import java.util.List;

public interface ProductService {

    // Save a product (create or update)
    Product saveProduct(Product product);

    // Get a product by its ID
    Product getProductById(Long productId);

    // Get a product as a ProductDTO by its ID
    ProductDTO getProductDTOById(Long productId);

    // Delete a product by its ID
    void deleteProduct(Long productId);

    // Get all products, optionally filtering by a search keyword
    List<Product> getAllProducts(String keyword);
}
