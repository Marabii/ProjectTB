package com.projetTB.projetTB.Auth.controller;


import com.projetTB.projetTB.Auth.DTO.AuthenticationRequest;
import com.projetTB.projetTB.Auth.DTO.AuthenticationResponse;
import com.projetTB.projetTB.Auth.DTO.RegisterRequest;
import com.projetTB.projetTB.Auth.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @Value("#{T(Boolean).parseBoolean('${development}')}")
    private Boolean development;

    @Value("#{T(java.lang.Integer).parseInt('${jwt.validFor}')}")
    private int validFor;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request, HttpServletResponse response) {
        // Perform the registration
        AuthenticationResponse jwtInstance = service.register(request);
        Cookie jwtCookie = getCookie(jwtInstance);
        response.addCookie(jwtCookie);

        ResponseEntity.ok(jwtInstance);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request, HttpServletResponse response) {
        // Perform the authentication
        AuthenticationResponse jwtInstance = service.authenticate(request);

        // Create and set the JWT cookie
        Cookie jwtCookie = getCookie(jwtInstance);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(jwtInstance);
    }

    private Cookie getCookie(AuthenticationResponse jwtInstance) {
        String jwtToken = jwtInstance.getToken();

        // Create and set the JWT cookie
        Cookie jwtCookie = new Cookie("jwtToken", jwtToken);
        jwtCookie.setHttpOnly(true);  // Secure the cookie from client-side scripts
        jwtCookie.setSecure(!development);    // Use this if your site is served over HTTPS
        jwtCookie.setPath("/");       // Make the cookie accessible to the entire site
        jwtCookie.setMaxAge(validFor / 1000); // Set the cookie to expire in 1 hour (adjust as needed)
        return jwtCookie;
    }

}

