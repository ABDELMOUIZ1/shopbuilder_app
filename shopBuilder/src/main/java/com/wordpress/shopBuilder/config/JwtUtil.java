package com.wordpress.shopBuilder.config;

import java.io.IOException;
import java.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtUtil {

    public static JsonNode decodeJwt(String jwtToken) throws IOException {
        String[] parts = jwtToken.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }

        String payload = parts[1];
        byte[] decodedBytes = Base64.getDecoder().decode(payload);
        String decodedPayload = new String(decodedBytes);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(decodedPayload);
    }
}
