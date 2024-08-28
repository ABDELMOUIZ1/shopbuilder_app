package com.wordpress.shopBuilder.controller;

import com.wordpress.shopBuilder.dto.BackgroundImageDto;
import com.wordpress.shopBuilder.dto.PackImageDto;
import com.wordpress.shopBuilder.dto.ProductImageDto;
import com.wordpress.shopBuilder.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class MediaController {

    @Autowired
    private MediaService mediaService;


    @PostMapping("/upload/images")
    public ResponseEntity<?> uploadImages(@RequestParam("files") MultipartFile[] files, @CookieValue("wpToken") String wpToken) throws IOException {



        List<ProductImageDto> imageUrls = mediaService.uploadImages(files, wpToken);
        System.out.println("imagesUrls" + imageUrls);
        return ResponseEntity.ok(imageUrls);
    }


    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @CookieValue("wpToken") String wpToken) throws IOException {

        try {
            Integer imageId = mediaService.uploadImage(file, wpToken);
            return ResponseEntity.ok(imageId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image: " + e.getMessage());
        }
    }



    @PostMapping("/upload/images2")
    public ResponseEntity<?> uploadImages2(@RequestParam("files") MultipartFile[] files, @CookieValue("wpToken") String wpToken) throws IOException {


        List<PackImageDto> imageUrls = mediaService.uploadImages2(files, wpToken);
        System.out.println("imagesUrls" + imageUrls);
        return ResponseEntity.ok(imageUrls);
    }

    @PostMapping("/upload/images3")
    public ResponseEntity<?> uploadImages3(@RequestParam("files") MultipartFile[] files, @CookieValue("wpToken") String wpToken) throws IOException {

        List<BackgroundImageDto> imageUrls = mediaService.uploadImages3(files, wpToken);
        System.out.println("imagesUrls" + imageUrls);
        return ResponseEntity.ok(imageUrls);
    }

}
