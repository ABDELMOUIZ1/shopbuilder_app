package com.wordpress.shopBuilder.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.wordpress.shopBuilder.config.JwtConfig;
import com.wordpress.shopBuilder.dto.ProductDto;
import com.wordpress.shopBuilder.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {


    @Autowired
    private ProductService productService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(@RequestBody ProductDto ProductDto, @CookieValue("wpToken") String wpToken, @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {

        System.out.println("wa tongooooooooooooooo controller");
        System.out.println("wa tongooooooooooooooo controller");
        System.out.println("wa tongooooooooooooooo controller");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7); // Extract JWT token from Authorization header

            // Validate JWT token
            if (jwtConfig.validateToken(jwtToken)) {

                return productService.addProduct(ProductDto, wpToken);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}