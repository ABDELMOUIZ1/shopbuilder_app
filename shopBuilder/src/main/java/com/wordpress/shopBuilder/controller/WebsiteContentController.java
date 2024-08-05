package com.wordpress.shopBuilder.controller;

import com.wordpress.shopBuilder.config.JwtConfig;
import com.wordpress.shopBuilder.dto.WebsiteContentDto;
import com.wordpress.shopBuilder.service.WebsiteContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class WebsiteContentController {

    @Autowired
    private WebsiteContentService websiteContentService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("/updateWebsite")
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "True")
    public ResponseEntity<String> updateWebsite(@RequestBody WebsiteContentDto websiteUpdateDto, @CookieValue("wpToken") String wpToken, @RequestHeader("Authorization") String authorizationHeader) {

        System.out.println("hahahahahahhahahhahahahahahah");
        System.out.println(websiteUpdateDto);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7); // Extract JWT token from Authorization header

            // Validate JWT token
            if (jwtConfig.validateToken(jwtToken)) {
                return websiteContentService.updateWebsite(websiteUpdateDto, wpToken);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
