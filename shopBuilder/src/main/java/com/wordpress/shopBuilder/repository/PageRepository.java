package com.wordpress.shopBuilder.repository;


import com.wordpress.shopBuilder.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {
}
