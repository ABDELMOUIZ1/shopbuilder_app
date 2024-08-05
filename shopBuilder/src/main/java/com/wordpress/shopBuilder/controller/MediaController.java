package com.wordpress.shopBuilder.controller;

import com.wordpress.shopBuilder.config.JwtConfig;
import com.wordpress.shopBuilder.dto.ProductImageDto;
import com.wordpress.shopBuilder.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "True")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("/upload/images")
    public ResponseEntity<?> uploadImages(@RequestParam("files") MultipartFile[] files,
                                          @CookieValue("wpToken") String wpToken,
                                          @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String jwtToken = authorizationHeader.substring(7); // Extract JWT token from Authorization header

        if (!jwtConfig.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        List<ProductImageDto> imageUrls = mediaService.uploadImages(files, wpToken);
        System.out.println("imagesUrls" + imageUrls);
        return ResponseEntity.ok(imageUrls);
    }


    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @CookieValue("wpToken") String wpToken,
                                         @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String jwtToken = authorizationHeader.substring(7); // Extract JWT token from Authorization header

        if (!jwtConfig.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        try {
            Integer imageId = mediaService.uploadImage(file, wpToken);
            return ResponseEntity.ok(imageId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image: " + e.getMessage());
        }
    }
}
