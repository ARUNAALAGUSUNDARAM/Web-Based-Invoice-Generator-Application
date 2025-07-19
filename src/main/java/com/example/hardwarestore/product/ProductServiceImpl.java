package com.example.hardwarestore.product;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    // Dependency: Repository to interact with the database
    private final ProductRepository productRepository;

    // Save a product (insert or update)
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Find a product by its ID
    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }

    // Delete a product by its ID
    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    // Get all products, with an optional search keyword
    @Override
    public List<Product> getAllProducts(String keyWord) {
        if (keyWord != null) {
            return productRepository.findAll(keyWord);
        }
        return productRepository.findAll();
    }

    // Get a product as a ProductDTO by its ID
    @Override
    public ProductDTO getProductDTOById(Long productId) {
        if (productId == null) {
            System.out.println("-----> productId is null");
        }
        return new ProductDTO(productRepository.findById(productId).orElse(null));
    }
}

