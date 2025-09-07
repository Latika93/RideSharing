package com.RideSharing.RideSharing.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.RideSharing.RideSharing.entity.User;
import com.RideSharing.RideSharing.entity.UserDTO;
import com.RideSharing.RideSharing.entity.VerificationToken;
import com.RideSharing.RideSharing.service.UserService;


@RestController
public class UserController {

  @Autowired
  private UserService _userService;


  @PostMapping("/register")
  public User registerUser(@RequestBody UserDTO user) {
    User registeredUser = _userService.registerUser(user);
    String verificationToken = UUID.randomUUID().toString();
    String verificationUrl = "http://localhost:9800/verifyRegistrationToken?token=" + verificationToken;
    System.out.println("Please verify your registration by clicking on the following link: " + verificationUrl);
    _userService.saveVerificationToken(registeredUser, verificationToken);
    return registeredUser;
  }

  @PostMapping("/verifyRegistrationToken")
  public String verifyRegistrationToken(@RequestParam String token) {
    VerificationToken verificationToken = _userService.verifyRegistrationToken(token);
    if (verificationToken != null) {
      _userService.enableUser(verificationToken);
      return "Token verification successful, user enabled. Please login to proceed.";
    } else {
      return "Token verification failed. Please try again.";
    }
  }

  @PostMapping("/signin")
  public String loginUser(@RequestParam String username, @RequestParam String password) {
    return _userService.loginUser(username, password);
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
    return "Test successful";
  }

  @GetMapping("/hello")
  public String hello() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return "Hello, World!";
  }

}
