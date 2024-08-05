package com.wordpress.shopBuilder.repository;


import com.wordpress.shopBuilder.model.Website;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteRepository extends JpaRepository<Website, Long> {
}
