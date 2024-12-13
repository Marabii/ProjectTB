package com.projetTB.projetTB.Auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetTB.projetTB.Auth.DTO.VerificationDetails;
import com.projetTB.projetTB.Auth.enums.ROLE;
import com.projetTB.projetTB.Auth.models.Users;
import com.projetTB.projetTB.Auth.repository.UsersRepository;
import com.projetTB.projetTB.Auth.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
public class GoogleOAuthController {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("#{T(Boolean).parseBoolean('${development}')}")
    private Boolean development;

    @Value("#{T(java.lang.Integer).parseInt('${jwt.validFor}')}")
    private int validFor;

    @Value("${front_end}")
    private String front_end;

    @Value("${back_end}")
    private String back_end;

    private final UsersRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public GoogleOAuthController(
            UsersRepository userRepository,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/google/callback")
    public void handleGoogleCallback(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error,
            HttpServletResponse response
    ) throws IOException {
        if (code == null || error != null) {
            redirectWithError(response, front_end, "Invalid request or authorization denied.");
            return;
        }

        try {
            String accessToken = getOauthAccessTokenGoogle(code);
            Map<String, String> userInfo = getProfileDetailsGoogle(accessToken);
            if (userInfo == null) {
                redirectWithError(response, front_end, "Failed to retrieve user information.");
                return;
            }
            String jwtToken = saveOrLogin(userInfo, userRepository);
            Cookie jwtCookie = createJwtCookie(jwtToken);
            response.addCookie(jwtCookie);
            response.sendRedirect(front_end);
        } catch (Exception e) {
            redirectWithError(response, front_end, "An unexpected error occurred: " + e.getMessage());
        }
    }

    private Cookie createJwtCookie(String jwtToken) {
        Cookie jwtCookie = new Cookie("jwtToken", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(!development);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(validFor / 1000);
        return jwtCookie;
    }

    private String getOauthAccessTokenGoogle(String code) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", back_end + "/google/callback");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);

        String url = "https://oauth2.googleapis.com/token";
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> responseMap = mapper.readValue(response.getBody(), Map.class);
            return responseMap.get("access_token");
        } catch (Exception e) {
            throw new Exception("Unable to get access token, please try again or use your Email to login");
        }
    }

    private Map<String, String> getProfileDetailsGoogle(String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            throw new Exception("Unable to get your profile data, please try again or use your Email to login");
        }
    }

    private String saveOrLogin(
            Map<String, String> userInfo,
            UsersRepository usersRepository
    ) {
        String userEmail = userInfo.get("email");

        // Find the user by Email
        Optional<Users> userOptional = usersRepository.findByEmail(userEmail);

        // Check if the user exists
        Users user = userOptional.orElseGet(() -> {
            // Safeguard against potential nulls and handle the verified_email field correctly
            Object verifiedEmailObj = userInfo.get("verified_email");
            boolean isVerified = false;

            if (verifiedEmailObj instanceof String) {
                isVerified = Boolean.parseBoolean((String) verifiedEmailObj);
            } else if (verifiedEmailObj instanceof Boolean) {
                isVerified = (Boolean) verifiedEmailObj;
            }

            VerificationDetails verificationDetails = new VerificationDetails();
            verificationDetails.setEmailVerified(isVerified);

            // Create and save a new user if they don't exist
            Users newUser = Users.builder()
                    .email(userEmail)
                    .isAuthenticatedByGoogle(true)
                    .username(userInfo.get("name"))
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .role(ROLE.USER)
                    .verificationDetails(verificationDetails)
                    .profilePicture(userInfo.get("picture"))
                    .build();
            return usersRepository.save(newUser); // Save the new user and return it
        });

        // Generate and return JWT token for the user
        return jwtService.generateToken(user);
    }

    private void redirectWithError(
            HttpServletResponse response,
            String baseUrl,
            String errorMessage
    ) throws IOException {
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect(baseUrl + "?error=" + encodedMessage);
    }
}
