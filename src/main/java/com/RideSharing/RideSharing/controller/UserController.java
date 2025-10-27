package com.RideSharing.RideSharing.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.RideSharing.RideSharing.entity.*;
import com.RideSharing.RideSharing.service.UserService;
import com.RideSharing.RideSharing.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "User Management", description = "APIs for user registration, authentication, and profile management")
public class UserController {

  @Autowired
  private UserService _userService;

  @Autowired
  private EmailService emailService;

  @PostMapping("/register")
  public User registerUser(@RequestBody UserDTO user) {
    User registeredUser = _userService.registerUser(user);
    String verificationToken = UUID.randomUUID().toString();
    String verificationUrl = "http://localhost:9800/verifyRegistrationToken?token=" + verificationToken;
    System.out.println("Please verify your registration by clicking on the following link: " + verificationUrl);
    _userService.saveVerificationToken(registeredUser, verificationToken);
    return registeredUser;
  }

  @PostMapping("/test")
  @PreAuthorize("hasRole('USER')")
  public String test() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    System.out.println("Authenticated user: " + username);
    authentication.getAuthorities().forEach(authority -> {
      System.out.println("User authority: " + authority.getAuthority());
    });
    if (authentication.getAuthorities().isEmpty()) {
      System.out.println("No authorities found for user: " + username);
      return "Test failed, no authorities found for user: " + username;
    }
    return "Test successful for user: " + username;
  }

  @GetMapping("/hello")
  public String hello() {
    return "Hello, World!";
  }

  // New Auth endpoints
  @Operation(summary = "Register a new rider", description = "Creates a new rider account with verification token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Rider registered successfully", 
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
      @ApiResponse(responseCode = "400", description = "Invalid registration data", 
          content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"Registration failed\",\"message\":\"Username already exists\"}")))
  })
  @PostMapping("/auth/register/rider")
  public ResponseEntity<?> registerRider(@RequestBody RiderRegistrationDTO riderDto) {
    try {
      User registeredUser = _userService.registerRider(riderDto);
      String verificationToken = UUID.randomUUID().toString();
      String verificationUrl = "http://localhost:9800/auth/verifyRegistrationToken?token=" + verificationToken;
      System.out.println("Please verify your registration by clicking on the following link: " + verificationUrl);
      _userService.saveVerificationToken(registeredUser, verificationToken);
      emailService.sendSimpleMail(
              registeredUser.getEmail(),
              "Welcome!",
              "Hey there 👋, your account has been created successfully. Click : " + verificationUrl
      );
      return ResponseEntity.ok(registeredUser);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("{\"error\":\"Registration failed\",\"message\":\"" + e.getMessage() + "\"}");
    }
  }

  @Operation(summary = "Register a new driver", description = "Creates a new driver account with verification token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Driver registered successfully", 
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
      @ApiResponse(responseCode = "400", description = "Invalid registration data", 
          content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"Registration failed\",\"message\":\"Username already exists\"}")))
  })
  @PostMapping("/auth/register/driver")
  public ResponseEntity<?> registerDriver(@RequestBody DriverRegistrationDTO driverDto) {
    try {
      User registeredUser = _userService.registerDriver(driverDto);
      String verificationToken = UUID.randomUUID().toString();
      String verificationUrl = "http://localhost:9800/auth/verifyRegistrationToken?token=" + verificationToken;
      System.out.println("Please verify your registration by clicking on the following link: " + verificationUrl);
      _userService.saveVerificationToken(registeredUser, verificationToken);
      emailService.sendSimpleMail(
              registeredUser.getEmail(),
              "Welcome!",
              "Hey Captain 👋, your account has been created successfully. Click : " + verificationUrl
      );
      return ResponseEntity.ok(registeredUser);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("{\"error\":\"Registration failed\",\"message\":\"" + e.getMessage() + "\"}");
    }
  }

  @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login successful", 
          content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"id\":1,\"username\":\"john_doe\",\"message\":\"Login successful\"}"))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials", 
          content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"Login failed\",\"message\":\"Invalid username or password\"}")))
  })
  @PostMapping("/auth/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDto) {
      try {
          System.out.println("Login attempt for username: " + loginDto.getUsername());

          String token = _userService.loginUser(loginDto.getUsername(), loginDto.getPassword());
          User user = _userService.findByUsername(loginDto.getUsername());

          Map<String, Object> response = new HashMap<>();
          response.put("token", token);
          response.put("id", user.getUserId());
          response.put("username", user.getUsername());
          response.put("message", "Login successful");

          return ResponseEntity.ok(response);

      } catch (Exception e) {
          System.out.println("Login failed: " + e.getMessage());
          Map<String, String> errorResponse = new HashMap<>();
          errorResponse.put("error", "Login failed");
          errorResponse.put("message", e.getMessage());
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
      }
  }


    // Alternative endpoint that still accepts form parameters (for testing)
  @PostMapping("/auth/login-form")
  public ResponseEntity<?> loginUserForm(@RequestParam String username, @RequestParam String password) {
    try {
      System.out.println("Form login attempt for username: " + username);
      String token = _userService.loginUser(username, password);
      return ResponseEntity.ok("{\"token\":\"" + token + "\",\"message\":\"Login successful\"}");
    } catch (Exception e) {
      System.out.println("Form login failed: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("{\"error\":\"Login failed\",\"message\":\"" + e.getMessage() + "\"}");
    }
  }

  @PostMapping("/auth/logout")
  public ResponseEntity<?> logoutUser() {
    // Clear security context
    SecurityContextHolder.clearContext();
    return ResponseEntity.ok("{\"message\":\"Logout successful\"}");
  }

  @GetMapping("/auth/verifyRegistrationToken")
  public ResponseEntity<?> verifyRegistrationToken(@RequestParam String token) {
    try {
      VerificationToken verificationToken = _userService.verifyRegistrationToken(token);
      if (verificationToken != null) {
        _userService.enableUser(verificationToken);
        return ResponseEntity.ok("{\"message\":\"Token verification successful, user enabled. Please login to proceed.\"}");
      } else {
        return ResponseEntity.badRequest().body("{\"error\":\"Token verification failed\",\"message\":\"Invalid or expired token\"}");
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("{\"error\":\"Token verification failed\",\"message\":\"" + e.getMessage() + "\"}");
    }
  }
}