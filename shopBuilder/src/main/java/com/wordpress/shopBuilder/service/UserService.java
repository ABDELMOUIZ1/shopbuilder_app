package com.wordpress.shopBuilder.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    private final WebClient webClient;

    @Autowired
    public UserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ResponseEntity<String> registerUser(UserRegistrationDto registrationDto) {
        // Check if the user already exists
        Optional<User> existingUser = userRepository.findByUsername(registrationDto.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("User already registered");
        }

        String username = registrationDto.getUsername();
        String password = registrationDto.getPassword();
        String domaineName = registrationDto.getDomaineName();

        // Check if the username and password match the WordPress account
        String wpToken = getWordPressToken(username, password, domaineName);
        if (wpToken == null) {
            return ResponseEntity.badRequest().body("Invalid WordPress credentials");
        }

        // Encode the password before saving
        registrationDto.setPassword(passwordEncoder.encode(password));

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

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        Optional<User> optionalUser = userRepository.findByUsername(loginDto.getUsername());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String domaineName = user.getDomaineName();

            String wpToken = getWordPressToken(username, password, domaineName);
            if (wpToken == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid WordPress credentials"));
            }

            // Generate JWT token with additional claims
            Instant instant = Instant.now();
            String scope = authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.joining(" "));

            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .subject(authentication.getName())
                    .issuedAt(instant)
                    .expiresAt(instant.plus(50, ChronoUnit.MINUTES))
                    .issuer("shopbuilder")
                    .claim("scope", scope)
                    .claim("userId", user.getIdUser())
                    .claim("email", user.getEmail())
                    .claim("domaineName", user.getDomaineName())
                    .claim("firstName", user.getFirstName())
                    .claim("lastName", user.getLastName())
                    .build();

            String jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();

            Map<String, String> response = new HashMap<>();
            response.put("wpToken", wpToken);
            response.put("jwt", jwt);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials"));
    }

    private String getWordPressToken(String username, String password, String domaineName) {
        String url = domaineName + "/wp-json/jwt-auth/v1/token?username=" + username + "&password=" + password;
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
