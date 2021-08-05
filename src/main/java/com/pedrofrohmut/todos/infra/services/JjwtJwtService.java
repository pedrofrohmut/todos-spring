package com.pedrofrohmut.todos.infra.services;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.crypto.SecretKey;

import com.pedrofrohmut.todos.domain.services.JwtService;
import com.pedrofrohmut.todos.infra.dtos.AuthTokenDto;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JjwtJwtService implements JwtService {

  public String generateToken(String userId) {
    var expiration = getExpirationDate();
    var signKey = getSignKey();
    var jws = buildJwt(userId, expiration, signKey);
    return jws;
  }

  private Date getExpirationDate() {
    var oneDay = 1000 * 60 * 60 * 24;
    var now = new Date().getTime();
    var expirationDate = new Date(now + oneDay);
    return expirationDate;
  }

  private String buildJwt(String userId, Date expires, SecretKey key) {
    return Jwts
      .builder()
      .signWith(key)
      .setExpiration(expires)
      .claim("userId", userId)
      .compact();
  }

  private AuthTokenDto decodeToken(String token) {
    final var key = getSignKey();
    final var parser = buildParser(key);
    final var claims = parser.parseClaimsJws(token);
    final var dto = new AuthTokenDto();
    dto.userId = (String) claims.getBody().get("userId");
    dto.exp = claims.getBody().getExpiration().getTime();
    return dto;
  }

  private SecretKey getSignKey() {
    final var resource = "application.properties";
    final var props = new Properties();
    try (
        final var propertiesFile = JwtService.class.getClassLoader().getResourceAsStream(resource)
    ) {
      if (propertiesFile == null) {
        throw new RuntimeException("Unable to find application properties");
      }
      props.load(propertiesFile);
      final var secret = (String) props.get("JWT_SECRET");
      final var key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
      return key;
    } catch (IOException e) {
      throw new RuntimeException(
          "Failed to get JWT_SECRET from application.properties: " + e.getMessage());
    }
  }

  private JwtParser buildParser(SecretKey key) {
    return Jwts
      .parserBuilder()
      .setSigningKey(key)
      .build();
  }

  public String getUserIdFromToken(String token) {
    final var decoded = decodeToken(token);
    return decoded.userId;
  }

}
