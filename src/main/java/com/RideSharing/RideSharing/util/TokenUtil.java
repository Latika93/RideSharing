package com.RideSharing.RideSharing.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import com.RideSharing.RideSharing.entity.User;


public class TokenUtil {

  public static String generateToken(User user, String role) {
    return Jwts.builder().subject(user.getUsername()).setIssuedAt(new Date()).setExpiration(
        new Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000))
        .claim("roles", "ROLE_" + user.getRole())

        .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, "secretKeyAirtribeTestingTokensecretKeyAirtribeTestingTokensecretKeyAirtribeTestingToken")
        .compact();

  }

  public static Claims validateSignedToken(String authorizationHeader) {
    try {
      Claims body = Jwts.parser()
          .setSigningKey("secretKeyAirtribeTestingTokensecretKeyAirtribeTestingTokensecretKeyAirtribeTestingToken")
          .build()
          .parseClaimsJws(authorizationHeader)
          .getBody();
      System.out.print("Claims: " + body);
      return body;
    } catch (SignatureException exception) {
      System.err.println("Invalid JWT signature: " + exception.getMessage());
      return null;
    } catch (io.jsonwebtoken.ExpiredJwtException exception) {
      System.err.println("JWT token is expired: " + exception.getMessage());
      return null;
    } catch (io.jsonwebtoken.MalformedJwtException exception) {
      System.err.println("Invalid JWT token: " + exception.getMessage());
      return null;
    } catch (io.jsonwebtoken.UnsupportedJwtException exception) {
      System.err.println("JWT token is unsupported: " + exception.getMessage());
      return null;
    } catch (IllegalArgumentException exception) {
      System.err.println("JWT claims string is empty: " + exception.getMessage());
      return null;
    }

  }
}
