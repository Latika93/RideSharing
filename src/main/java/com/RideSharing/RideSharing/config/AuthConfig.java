package com.RideSharing.RideSharing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
public class AuthConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(11);
  }


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
    try {
      httpSecurity.csrf(csrf -> csrf.disable())
          .authorizeHttpRequests(authorizeRequests -> authorizeRequests
              .requestMatchers("/register", "/verifyRegistrationToken", "/signin", "/test", 
                              "/auth/register/rider", "/auth/register/driver", "/auth/login", 
                              "/auth/logout", "/auth/verifyRegistrationToken", "/hello")
              .permitAll()
              .requestMatchers("/driver/**").hasRole("DRIVER")
              .requestMatchers("/rider/**").hasRole("RIDER")
              .requestMatchers("/admin/**").hasRole("ADMIN")
              .anyRequest()
              .authenticated())
          .formLogin(formLogin -> formLogin.defaultSuccessUrl("/hello", true).permitAll())
          .addFilterBefore(new AuthJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
      return httpSecurity.build();
    } catch (Exception e) {
      throw new RuntimeException("Error configuring security filter chain", e);
    }
  }
  
}
