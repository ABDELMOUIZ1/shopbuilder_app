package com.wordpress.shopBuilder.controller;

import com.wordpress.shopBuilder.dto.PackDto;
import com.wordpress.shopBuilder.service.PackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/packs")
public class PackController {

    @Autowired
    private PackService packService;


    @PostMapping("/add")
    public ResponseEntity<String> addPack(@RequestBody PackDto packDto, @CookieValue("wpToken") String wpToken, @RequestHeader("Authorization") String authorizationHeader) throws IOException {

                return packService.addPack(packDto, wpToken);
            }

}
