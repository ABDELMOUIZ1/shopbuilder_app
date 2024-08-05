package com.wordpress.shopBuilder.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idWebsite;

    private String websiteDomaineName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "website", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private WebsiteContent websiteContent;

    // Getters and Setters
}
