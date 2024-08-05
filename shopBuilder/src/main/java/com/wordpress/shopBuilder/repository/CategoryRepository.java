package com.wordpress.shopBuilder.repository;


import com.wordpress.shopBuilder.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
