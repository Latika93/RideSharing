package com.RideSharing.RideSharing.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class AuthConfig {

  @Autowired
  private LoggingFilter loggingFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(11);
  }

  @Bean
  public AuthJwtAuthenticationFilter authJwtAuthenticationFilter() {
    return new AuthJwtAuthenticationFilter();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
            // Disable CSRF
            .csrf(csrf -> csrf.disable())

            // Configure CORS properly (don't just disable it)
            .cors(cors -> cors.disable())

            // Stateless session
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Add custom filters
            .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(authJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

            // Configure authorization
            .authorizeHttpRequests(auth -> auth
                    // Allow ALL auth endpoints without authentication
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/register", "/hello", "/error").permitAll()

                    // Role-based access (only after login works)
                    .requestMatchers("/driver/**").hasRole("DRIVER")
                    .requestMatchers("/rider/**").hasRole("RIDER")
                    .requestMatchers("/admin/**").hasRole("ADMIN")

                    // Protected endpoints that need authentication
                    .requestMatchers("/test").hasRole("USER")

                    // All other requests need auth
                    .anyRequest().authenticated())

            // COMPLETELY DISABLE form login
            .formLogin(form -> form.disable())


            // DISABLE http basic
            .httpBasic(basic -> basic.disable())

            // Disable default login page
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                      response.setContentType("application/json");
                      response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Please provide valid JWT token\"}");
                    }))

            .build();
  }
}