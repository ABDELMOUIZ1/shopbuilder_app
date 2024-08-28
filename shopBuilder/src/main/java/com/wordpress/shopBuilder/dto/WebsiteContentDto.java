package com.wordpress.shopBuilder.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteContentDto {

    private String title;
    private Integer site_logo;
    private String backgroundColor;
    private String fontFamily;
    private String backgroundImageHome;
    private String backgroundImageContact;
    private String backgroundImageAbout;
    private String homePic; // Changed to MultipartFile
    private String homeDesc;
}



