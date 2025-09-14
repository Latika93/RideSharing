package com.RideSharing.RideSharing.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import com.RideSharing.RideSharing.util.TokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthJwtAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    System.out.println("[JWT Filter] Processing: " + request.getMethod() + " " + request.getRequestURI());

    String authorizationHeader = request.getHeader("Authorization");

    // Check if the Authorization header is present and starts with "Bearer "
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      System.out.println("[JWT Filter] No valid Authorization header found");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\":\"Missing or invalid Authorization header\",\"message\":\"Please provide Bearer token\"}");
      return;
    }

    try {
      // Extract token (remove "Bearer " prefix)
      String token = authorizationHeader.substring(7);
      Claims fetchedClaims = TokenUtil.validateSignedToken(token);

      if(fetchedClaims == null)  {
        System.out.println("[JWT Filter] Token validation failed");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Invalid token\",\"message\":\"JWT token is invalid or expired\"}");
        return;
      }

      String username = fetchedClaims.getSubject();
      String role = fetchedClaims.get("roles", String.class);
      System.out.println("[JWT Filter] Authenticated user: " + username + " with role: " + role);

      List<SimpleGrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(role));
      UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(username, null, authorityList);

      SecurityContextHolder.getContext().setAuthentication(authentication);
      System.out.println("[JWT Filter] Authentication set successfully");

    } catch (Exception e) {
      System.out.println("[JWT Filter] Exception during token processing: " + e.getMessage());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\":\"Token processing failed\",\"message\":\"" + e.getMessage() + "\"}");
      return;
    }

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // Skip filtering for specific paths and OPTIONS requests (CORS preflight)
    String path = request.getRequestURI();
    String method = request.getMethod();
    
    boolean shouldSkip = path.contains("/auth/") ||
            path.contains("/register") ||
            path.contains("/hello") ||
            path.contains("/error") ||
            "OPTIONS".equals(method); // Skip JWT validation for CORS preflight requests

    System.out.println("[JWT Filter] Should skip filtering for " + method + " " + path + ": " + shouldSkip);
    return shouldSkip;
  }
}