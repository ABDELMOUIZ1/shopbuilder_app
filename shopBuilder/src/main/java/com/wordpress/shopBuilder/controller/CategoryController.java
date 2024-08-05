package com.wordpress.shopBuilder.controller;

import com.wordpress.shopBuilder.config.JwtConfig;
import com.wordpress.shopBuilder.dto.CategoryDto;
import com.wordpress.shopBuilder.dto.CategoryResponseDto;
import com.wordpress.shopBuilder.model.Category;
import com.wordpress.shopBuilder.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/categories")

public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private JwtConfig jwtConfig;


    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {

        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<String> addCategory(@RequestBody CategoryDto categoryDto, @CookieValue("wpToken") String wpToken, @RequestHeader("Authorization") String authorizationHeader) throws IOException {

        System.out.println("controller");
        System.out.println(categoryDto);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7); // Extract JWT token from Authorization header

            // Validate JWT token
            if (jwtConfig.validateToken(jwtToken)) {

                return categoryService.addCategory(categoryDto, wpToken);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
