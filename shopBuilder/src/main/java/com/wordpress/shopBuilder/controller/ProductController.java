package com.wordpress.shopBuilder.controller;



import com.wordpress.shopBuilder.dto.ProductDto;
import com.wordpress.shopBuilder.dto.ProductResponseDto;
import com.wordpress.shopBuilder.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {


    @Autowired
    private ProductService productService;


    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(@RequestBody ProductDto ProductDto, @CookieValue("wpToken") String wpToken) throws IOException {

        System.out.println("wa tongooooooooooooooo controller");
        System.out.println("wa tongooooooooooooooo controller");
        System.out.println("wa tongooooooooooooooo controller");


                return productService.addProduct(ProductDto, wpToken);
            }


    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {

        System.out.println("tongooooooo get All Products");

        System.out.println("tongoooooo");
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}