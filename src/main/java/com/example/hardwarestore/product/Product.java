package com.example.hardwarestore.product;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue
    private Long productId;

    @NotNull(message = "The product name cannot be null")
    @NotEmpty(message = "The product name cannot be empty")
    @Column(name = "name")
    private String name;

    @NotNull(message = "The product price cannot be null")
    @DecimalMin(value = "0.0", message = "The price must be greater than 0")
    @Column(name = "price")
    private Double price;

    @NotNull(message = "The product category cannot be null")
    @NotEmpty(message = "The product category cannot be empty")
    @Column(name = "category")
    private String category;

    @NotNull(message = "The product stock cannot be null")
    @Min(value = 0, message = "The stock cannot be negative")
    @Column(name = "stock")
    private Integer stock;

    @Column(name = "description")
    private String description;

    @NotNull(message = "The product status cannot be null")
    @Column(name = "status")
    private Boolean status = true;
}