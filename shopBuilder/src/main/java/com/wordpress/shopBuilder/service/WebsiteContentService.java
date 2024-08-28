package com.wordpress.shopBuilder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wordpress.shopBuilder.config.JwtUtil;
import com.wordpress.shopBuilder.dto.*;
import com.wordpress.shopBuilder.model.WebsiteContent;

import com.wordpress.shopBuilder.repository.WebsiteContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Service
public class WebsiteContentService {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteContentService.class);
    private final WebClient webClient;

    @Autowired
    private WebsiteContentRepository websiteContentRepository;

    @Autowired
    public WebsiteContentService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ResponseEntity<String> updateWebsite(WebsiteContentDto websiteContentDto, String wpToken) throws IOException {
        // Convert WebsiteContentDto to WebsiteContent entity
        WebsiteContent websiteContent = convertDto(websiteContentDto);
        System.out.println("tongo image d acceuil"   +   websiteContentDto.getHomePic());

        // Save the WebsiteContent entity to the database
        websiteContentRepository.save(websiteContent);

        // Create and call the update functions concurrently
        Flux<ResponseEntity<String>> responses = Flux.merge(
                updateSectionBackground(new SectionBackgroundDto("Home", websiteContentDto.getBackgroundImageHome()), wpToken),
                updateSectionBackground(new SectionBackgroundDto("Contact", websiteContentDto.getBackgroundImageContact()), wpToken),
                updateSectionBackground(new SectionBackgroundDto("About", websiteContentDto.getBackgroundImageAbout()), wpToken),
                updateGlobalStyles(new GlobalStylesDto(websiteContentDto.getBackgroundColor(), websiteContentDto.getFontFamily()), wpToken),
                updateNavContent(new NavContentDto(websiteContentDto.getTitle(), websiteContentDto.getSite_logo()), wpToken),
                updateHomePic(websiteContentDto.getHomePic(), wpToken), // Pass the URL
                updateHomeDesc(websiteContentDto.getHomeDesc(), wpToken)
        );

        // Aggregate responses and check if all were successful
        return responses
                .collectList()
                .map(responsesList -> {
                    boolean allSuccessful = responsesList.stream()
                            .allMatch(response -> response.getStatusCode().is2xxSuccessful());

                    if (allSuccessful) {
                        logger.info("Website content updated successfully.");
                        return ResponseEntity.ok("Website content updated successfully.");
                    } else {
                        logger.error("Failed to update website content.");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to update website content.");
                    }
                })
                .block(); // Blocking for simplicity; consider using async handling for real use cases
    }


    private Mono<ResponseEntity<String>> updateSectionBackground(SectionBackgroundDto sectionBackgroundDto, String wpToken) throws IOException {
// Decode the JWT to get the domain name
        JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
        String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field


        System.out.println("service");
        String url = domaineName + "/wp-json/custom/v1/section-background";
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sectionBackgroundDto), SectionBackgroundDto.class)
                .retrieve()
                .toEntity(String.class)
                .doOnSuccess(response -> logger.info("Updated section background: " + response.getBody()))
                .doOnError(error -> logger.error("Error updating section background: ", error));
    }

    private Mono<ResponseEntity<String>> updateGlobalStyles(GlobalStylesDto globalStylesDto, String wpToken) throws IOException {
        // Decode the JWT to get the domain name
        JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
        String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field

        String url = domaineName + "/wp-json/custom/v1/global-styles";

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(globalStylesDto), GlobalStylesDto.class)
                .retrieve()
                .toEntity(String.class)
                .doOnSuccess(response -> logger.info("Updated global styles: " + response.getBody()))
                .doOnError(error -> logger.error("Error updating global styles: ", error));
    }

    private Mono<ResponseEntity<String>> updateNavContent(NavContentDto navContentDto, String wpToken) throws IOException {
        // Decode the JWT to get the domain name
        JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
        String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field

        String url = domaineName + "/wp-json/wp/v2/settings";

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(navContentDto), NavContentDto.class)
                .retrieve()
                .toEntity(String.class)
                .doOnSuccess(response -> logger.info("Updated navigation content: " + response.getBody()))
                .doOnError(error -> logger.error("Error updating navigation content: ", error));
    }

    private Mono<ResponseEntity<String>> updateHomePic(String homePicUrl, String wpToken) throws IOException {
        System.out.println("tongo image d acceuil"   +   homePicUrl);
        // Decode the JWT to get the domain name
        JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
        String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field

        String url = domaineName + "/wp-json/custom/v1/home-pic";

        // Create an instance of HomePicDto with the URL
        HomePicDto homePicDto = new HomePicDto(homePicUrl);

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(homePicDto), HomePicDto.class)
                .retrieve()
                .toEntity(String.class)
                .doOnSuccess(response -> logger.info("Updated home picture: " + response.getBody()))
                .doOnError(error -> logger.error("Error updating home picture: ", error));
    }





    private Mono<ResponseEntity<String>> updateHomeDesc(String homeDesc, String wpToken) throws IOException {
        // Decode the JWT to get the domain name
        JsonNode decodedJwt = JwtUtil.decodeJwt(wpToken);
        String domaineName = decodedJwt.get("iss").asText(); // Extract the "iss" field

        String url = domaineName + "/wp-json/custom/v1/home-desc";

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wpToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new HomeDescDto(homeDesc)), HomeDescDto.class)
                .retrieve()
                .toEntity(String.class)
                .doOnSuccess(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        logger.info("Updated home description successfully: " + response.getBody());
                    } else {
                        logger.warn("Failed to update home description. Status code: " + response.getStatusCode() +
                                ". Response body: " + response.getBody());
                    }
                })
                .doOnError(error -> logger.error("Error updating home description: ", error));
    }



    private WebsiteContent convertDto(WebsiteContentDto websiteContentDto) {
        WebsiteContent websiteContent = new WebsiteContent();
        websiteContent.setWebSiteName(websiteContentDto.getTitle());
        websiteContent.setLogo(websiteContentDto.getSite_logo());
        websiteContent.setThemeColor(websiteContentDto.getBackgroundColor());
        websiteContent.setFontStyle(websiteContentDto.getFontFamily());
        websiteContent.setBackgroundImageHome(websiteContentDto.getBackgroundImageHome());
        websiteContent.setBackgroundImageContact(websiteContentDto.getBackgroundImageContact());
        websiteContent.setBackgroundImageAbout(websiteContentDto.getBackgroundImageAbout());
        websiteContent.setHomePic(websiteContentDto.getHomePic());
        websiteContent.setHomeDesc(websiteContentDto.getHomeDesc());

        return websiteContent;
    }
}