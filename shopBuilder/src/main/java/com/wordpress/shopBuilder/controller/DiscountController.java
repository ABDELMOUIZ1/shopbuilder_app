package com.wordpress.shopBuilder.controller;


import com.wordpress.shopBuilder.dto.DiscountDto;
import com.wordpress.shopBuilder.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/coupons")
public class DiscountController {

    @Autowired
    private DiscountService discountService;



    @PostMapping("/add")
    public ResponseEntity<String> addDiscount(@RequestBody DiscountDto discountDto, @CookieValue("wpToken") String wpToken) throws IOException {
        System.out.println("discount: " + discountDto);



                return discountService.addDiscount(discountDto, wpToken);

        }


}


