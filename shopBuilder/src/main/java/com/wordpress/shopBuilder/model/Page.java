package com.wordpress.shopBuilder.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPage;

    private String bannerUrl;
    private String pageName;
    private String content;

    @ManyToOne
    @JoinColumn(name = "website_content_id")
    private WebsiteContent websiteContent;

    // Getters and Setters
}
