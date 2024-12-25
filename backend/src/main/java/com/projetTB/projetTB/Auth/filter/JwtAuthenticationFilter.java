package com.projetTB.projetTB.Auth.filter;

import com.projetTB.projetTB.Auth.exceptions.ExpiredTokenException;
import com.projetTB.projetTB.Auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Specify the paths that should not be filtered
        return !path.startsWith("/api/protected/"); // Only filter paths that start with /api/protected/
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // If this is a preflight request, return 200 OK and skip
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        try {
            String token = extractToken(request);
            if (token == null) {
                System.out.println("Authorization token missing or invalid format.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No Authorization token or invalid format");
                return;
            }
            authenticateUser(token, request);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handleAuthenticationException(e, response);
        }
    }

    private String extractToken(HttpServletRequest request) {
        // First, try to get the token from the Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            if (authHeader.startsWith("Bearer ")) {
                // Remove the "Bearer " prefix and return the token
                return authHeader.substring(7); // Extract the token after "Bearer "
            } else {
                // If it doesn't start with "Bearer ", return the header as is
                return authHeader;
            }
        }

        // If no valid Authorization header, try cookies
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies()).filter(cookie -> "jwtToken".equals(cookie.getName())).findFirst()
                    .map(Cookie::getValue).orElse(null);
        }

        return null; // No token found
    }

    private void authenticateUser(String token, HttpServletRequest request) throws Exception {
        String userEmail = jwtService.extractUsername(token);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                throw new SecurityException("Invalid token");
            }
        }
    }

    private void handleAuthenticationException(Exception e, HttpServletResponse response) throws IOException {
        System.out.println("Failed to authenticate user: " + e.getMessage());
        if (e instanceof ExpiredTokenException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired token");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }

}
