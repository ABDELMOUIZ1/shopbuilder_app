package com.wordpress.shopBuilder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/updateWebsite").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/addProduct").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/upload/image").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/upload/images").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/updateWebsite").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()




                        .anyRequest().authenticated()
                )
                .formLogin()
                .and()
                .csrf().disable();

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
