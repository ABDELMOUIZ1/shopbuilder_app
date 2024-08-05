package com.wordpress.shopBuilder.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wordpress.shopBuilder.config.JwtConfig;
import com.wordpress.shopBuilder.dto.DiscountDto;
import com.wordpress.shopBuilder.dto.ProductDto;
import com.wordpress.shopBuilder.service.DiscountService;
import com.wordpress.shopBuilder.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
public class DiscountController {

        @Autowired
        private DiscountService discountService;

    @Autowired
    private JwtConfig jwtConfig;

        @PostMapping("/add")
        public ResponseEntity<String> addDiscount(@RequestBody DiscountDto discountDto, @CookieValue("wpToken") String wpToken, @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwtToken = authorizationHeader.substring(7); // Extract JWT token from Authorization header

                // Validate JWT token
                if (jwtConfig.validateToken(jwtToken)) {

                    return discountService.addDiscount(discountDto, wpToken);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }


