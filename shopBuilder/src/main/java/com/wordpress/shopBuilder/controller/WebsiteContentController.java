package com.wordpress.shopBuilder.controller;

import com.wordpress.shopBuilder.dto.WebsiteContentDto;
import com.wordpress.shopBuilder.service.WebsiteContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class WebsiteContentController {

    @Autowired
    private WebsiteContentService websiteContentService;

    @Autowired
    private HttpServletRequest request;



    @PostMapping("/updateWebsite")
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "True")
    public ResponseEntity<String> updateWebsite(@RequestBody WebsiteContentDto websiteUpdateDto, @CookieValue("wpToken") String wpToken) throws IOException {

        System.out.println("hahahahahahhahahhahahahahahah");

        return websiteContentService.updateWebsite(websiteUpdateDto, wpToken);

    }
}