package com.wordpress.shopBuilder.service;

import com.wordpress.shopBuilder.config.JwtConfig;
import com.wordpress.shopBuilder.dto.UserLoginDto;
import com.wordpress.shopBuilder.dto.UserRegistrationDto;
import com.wordpress.shopBuilder.model.User;
import com.wordpress.shopBuilder.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    private final WebClient webClient;

    @Autowired
    public UserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost").build();
    }

    public ResponseEntity<String> registerUser(UserRegistrationDto registrationDto) {
        String username = registrationDto.getUsername();
        String password = registrationDto.getPassword();

        // Check if the user already exists
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("User already registered");
        }

        // Check if the username and password match the WordPress account
        String wpToken = getWordPressToken(username, password);
        if (wpToken == null) {
            return ResponseEntity.badRequest().body("Invalid WordPress credentials");
        }

        // Encode the password before saving
        registrationDto.setPassword(bCryptPasswordEncoder.encode(password));

        // Save user to the database
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setUsername(registrationDto.getUsername());
        user.setPassword(registrationDto.getPassword());
        user.setEmail(registrationDto.getEmail());
        user.setDomaineName(registrationDto.getDomaineName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<Map<String, String>> loginUser(UserLoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                String wpToken = getWordPressToken(username, password);
                if (wpToken == null) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Invalid WordPress credentials"));
                }

                // Generate JWT token
                String jwt = jwtConfig.generateToken(user.getIdUser(), user.getEmail(), user.getDomaineName(), user.getFirstName(), user.getLastName());

                Map<String, String> response = new HashMap<>();
                response.put("wpToken", wpToken);
                response.put("jwt", jwt);
                System.out.println(response);

                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials"));
    }

    private String getWordPressToken(String username, String password) {
        String url = "/wordpress/wp-json/jwt-auth/v1/token?username=" + username + "&password=" + password;
        Mono<WordPressTokenResponse> responseMono = webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(WordPressTokenResponse.class);

        WordPressTokenResponse response = responseMono.block();
        return response != null ? response.getToken() : null;
    }

    @Setter
    @Getter
    private static class WordPressTokenResponse {
        private String token;
    }
}
