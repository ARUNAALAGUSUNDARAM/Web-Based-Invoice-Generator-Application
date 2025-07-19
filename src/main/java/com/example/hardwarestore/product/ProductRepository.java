package com.example.hardwarestore.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query to search for products where the name, category, or description
    // contains the given keyword.
    @Query("SELECT p FROM Product p WHERE"
            + " CONCAT(p.name, ' ', p.category, ' ', p.description)"
            + " LIKE %?1%")
    List<Product> findAll(String keyword);
}
