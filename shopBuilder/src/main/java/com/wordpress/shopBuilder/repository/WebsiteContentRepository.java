package com.wordpress.shopBuilder.repository;


import com.wordpress.shopBuilder.model.WebsiteContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteContentRepository extends JpaRepository<WebsiteContent, Long> {
}
