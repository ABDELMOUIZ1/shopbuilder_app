package com.wordpress.shopBuilder.repository;


import com.wordpress.shopBuilder.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
