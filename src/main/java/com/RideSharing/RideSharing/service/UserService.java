package com.RideSharing.RideSharing.service;

import com.RideSharing.RideSharing.entity.User;
import com.RideSharing.RideSharing.entity.UserDTO;
import com.RideSharing.RideSharing.entity.VerificationToken;
import com.RideSharing.RideSharing.repository.UserRepository;
import com.RideSharing.RideSharing.repository.VerificationTokenRepository;
import com.RideSharing.RideSharing.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository _userRepository;

  @Autowired
  private VerificationTokenRepository _verificationTokenRepository;

  @Autowired
  private PasswordEncoder _passwordEncoder;

  public UserService() {

  }

  public User registerUser(UserDTO userDto) {
      User user = new User();
      user.setUsername(userDto.getUsername());
      user.setPassword(_passwordEncoder.encode(userDto.getPassword()));
      user.setEnabled(false);
      user.setRole("ADMIN");
      return _userRepository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User fetchedUser = _userRepository.findByUsername(username);
    if (fetchedUser == null) {
      throw new UsernameNotFoundException("User not found with username: " + username);
    }

    return org.springframework.security.core.userdetails.User
        .withUsername(fetchedUser.getUsername())
        .password(fetchedUser.getPassword())
        .roles(fetchedUser.getRole())
        .disabled(false)
        .build();

  }

  public void saveVerificationToken(User registeredUser, String verificationToken) {
    VerificationToken token = new VerificationToken();
    token.setToken(verificationToken);
    token.setUser(registeredUser);
    token.setExpiryDate(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)); // 24 hours
    _verificationTokenRepository.save(token);
  }

  public VerificationToken verifyRegistrationToken(String token) {
    VerificationToken storedToken = _verificationTokenRepository.findByToken(token);
    if (storedToken == null) {
      return null;
    }

    long registeredExpiryTime = storedToken.getExpiryDate().getTime();
    if (registeredExpiryTime < System.currentTimeMillis()) {
      return null;
    }

    return storedToken;

  }

  public void enableUser(VerificationToken token) {
    User fetchedUser = token.getUser();
    fetchedUser.setEnabled(true);
    _userRepository.save(fetchedUser);
    _verificationTokenRepository.delete(token);
  }

  public String loginUser(String username, String password) {
    User user = _userRepository.findByUsername(username);
    if (user == null) {
      return "User not found";
    }

    if (!user.isEnabled()) {
      return "User is not enabled, Please verify your account";
    }
    String passwordFetched = user.getPassword();
    boolean isMatch = _passwordEncoder.matches(password, passwordFetched);
    if (!isMatch) {
      return "Invalid password";
    }

    return TokenUtil.generateToken(user, user.getRole());
  }
}
