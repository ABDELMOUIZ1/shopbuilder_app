package com.wordpress.shopBuilder.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WebsiteContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idWebsiteContent;

    private String webSiteName;
    private Integer logo;
    private String themeColor;
    private String fontStyle;
    private String BackgroundImageHome;
    private String BackgroundImageContact;
    private String BackgroundImageAbout;
    private String homePic; // Changed to MultipartFile
    private String homeDesc;

    @OneToOne
    @JoinColumn(name = "website_id")
    private Website website;

    @OneToMany(mappedBy = "websiteContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Category> categories;

    @OneToMany(mappedBy = "websiteContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Product> products;

    @OneToMany(mappedBy = "websiteContent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Page> pages;

    // Getters and Setters
}
