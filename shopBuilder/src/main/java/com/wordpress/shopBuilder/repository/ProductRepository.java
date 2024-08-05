package com.wordpress.shopBuilder.repository;


import com.wordpress.shopBuilder.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
