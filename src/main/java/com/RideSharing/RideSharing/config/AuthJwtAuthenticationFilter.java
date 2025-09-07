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


public class AuthJwtAuthenticationFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authorizationHeader = request.getHeader("authorization");

    // Check if the Authorization header is present
    if (authorizationHeader == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Missing Authorization header");
      return;
    }

    Claims fetchedClaims = TokenUtil.validateSignedToken(authorizationHeader);
    if(fetchedClaims == null)  {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Invalid user token");
      return;
    }

    String username = fetchedClaims.getSubject();
    String role = fetchedClaims.get("roles", String.class);
    List<SimpleGrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(role));
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(username, null, authorityList);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);

  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // Skip filtering for specific paths
    String path = request.getRequestURI();
    return path.contains("register") || path.contains("signin") || path.contains("verifyRegistration");
  }
}

// Servlet -> Filter1 -> Filter2 -> Filter3 -> Controller -> Service -> Repository
